package com.tianshaokai.camera.cameraview.engine.orchestrator

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.tianshaokai.camera.cameraview.LOG
import com.tianshaokai.camera.cameraview.internal.WorkerHandler
import java.util.ArrayDeque
import java.util.Locale
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException

abstract class CameraOrchestrator(protected val callback: Callback) {

    protected val jobs: ArrayDeque<Token> = ArrayDeque()
    protected val lock = Any()
    private val delayedJobs: MutableMap<String, Runnable> = HashMap()

    init {
        ensureToken()
    }

    interface Callback {
        fun getJobWorker(name: String): WorkerHandler
        fun handleJobException(name: String, exception: Exception)
    }

    protected data class Token(val name: String, val task: Task<*>)

    fun schedule(name: String, handleException: Boolean, runnable: Runnable): Task<Void> {
        return schedule(name, handleException, Callable<Task<Void>> {
            runnable.run()
            Tasks.forResult(null)
        })
    }

    fun <T> schedule(name: String, handleException: Boolean, callable: Callable<Task<T>>): Task<T> {
        LOG.i(name.toUpperCase(Locale.ROOT), "- Scheduling.")
        val taskCompletionSource = TaskCompletionSource<T>()
        val jobWorker = callback.getJobWorker(name)

        synchronized(lock) {
            applyCompletionListener(jobs.last.task, jobWorker, OnCompleteListener { task ->
                synchronized(lock) {
                    jobs.removeFirst()
                    ensureToken()
                }
                try {
//                    LOG.i(name.uppercase(), "- Executing.")
                    applyCompletionListener(callable.call(), jobWorker, OnCompleteListener { resultTask ->
                        val exception = resultTask.exception
                        if (exception != null) {
//                            LOG.w(name.uppercase(), "- Finished with ERROR.", exception)
                            if (handleException) {
                                callback.handleJobException(name, exception)
                            }
                            taskCompletionSource.trySetException(exception)
                        } else if (resultTask.isCanceled) {
//                            LOG.i(name.uppercase(), "- Finished because ABORTED.")
                            taskCompletionSource.trySetException(CancellationException())
                        } else {
//                            LOG.i(name.uppercase(), "- Finished.")
                            taskCompletionSource.trySetResult(resultTask.result)
                        }
                    })
                } catch (e: Exception) {
//                    LOG.i(name.uppercase(), "- Finished.", e)
                    if (handleException) {
                        callback.handleJobException(name, e)
                    }
                    taskCompletionSource.trySetException(e)
                }
            })
            jobs.addLast(Token(name, taskCompletionSource.task))
        }
        return taskCompletionSource.task
    }

    fun scheduleDelayed(name: String, delayMillis: Long, runnable: Runnable) {
        val delayedRunnable = Runnable {
            schedule(name, true, runnable)
            synchronized(lock) {
                if (delayedJobs.containsValue(this)) {
                    delayedJobs.remove(name)
                }
            }
        }
        synchronized(lock) {
            delayedJobs[name] = delayedRunnable
            callback.getJobWorker(name).post(delayMillis, delayedRunnable)
        }
    }

    fun remove(name: String) {
        synchronized(lock) {
            delayedJobs[name]?.let {
                callback.getJobWorker(name).remove(it)
                delayedJobs.remove(name)
            }
            while (jobs.remove(Token(name, Tasks.forResult(null)))) {
                // Remove all matching tokens
            }
            ensureToken()
        }
    }

    fun reset() {
        synchronized(lock) {
            val allKeys = ArrayList<String>()
            allKeys.addAll(delayedJobs.keys)
            jobs.forEach { allKeys.add(it.name) }
            allKeys.forEach { remove(it) }
        }
    }

    private fun ensureToken() {
        synchronized(lock) {
            if (jobs.isEmpty()) {
                jobs.add(Token("BASE", Tasks.forResult(null)))
            }
        }
    }

    companion object {
//        protected val LOG = CameraLogger.create(CameraOrchestrator::class.java.simpleName)

        private fun <T> applyCompletionListener(
            task: Task<T>,
            workerHandler: WorkerHandler,
            onCompleteListener: OnCompleteListener<T>
        ) {
            if (task.isComplete) {
                workerHandler.run {
                    onCompleteListener.onComplete(task)
                }
            } else {
                task.addOnCompleteListener(workerHandler.getExecutor(), onCompleteListener)
            }
        }
    }
}
