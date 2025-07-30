package com.tianshaokai.camera.cameraview.internal

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import java.lang.ref.WeakReference
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor


class WorkerHandler private constructor(private val name: String) {

    private val handler: Handler
    private val thread: HandlerThread
    private val executor: Executor

    init {
        thread = object : HandlerThread(name) {
            override fun toString(): String {
                return super.toString() + "[${threadId}]"
            }
        }
        thread.isDaemon = true
        thread.start()
        handler = Handler(thread.looper)
        executor = Executor { runnable -> run(runnable) }

        val latch = CountDownLatch(1)
        post(Runnable { latch.countDown() })
        try {
            latch.await()
        } catch (e: InterruptedException) {
            // Handle interruption
        }
    }

    fun run(runnable: Runnable) {
        if (Thread.currentThread() == thread) {
            runnable.run()
        } else {
            post(runnable)
        }
    }

    fun <T> run(callable: Callable<T>): Task<T> {
        return if (Thread.currentThread() == thread) {
            try {
                Tasks.forResult(callable.call())
            } catch (e: Exception) {
                Tasks.forException(e)
            }
        } else {
            post(callable)
        }
    }

    fun post(runnable: Runnable) {
        handler.post(runnable)
    }

    fun <T> post(callable: Callable<T>): Task<T> {
        val taskCompletionSource = TaskCompletionSource<T>()
        post(Runnable {
            try {
                taskCompletionSource.trySetResult(callable.call())
            } catch (e: Exception) {
                taskCompletionSource.trySetException(e)
            }
        })
        return taskCompletionSource.task
    }

    fun post(delayMillis: Long, runnable: Runnable) {
        handler.postDelayed(runnable, delayMillis)
    }

    fun remove(runnable: Runnable) {
        handler.removeCallbacks(runnable)
    }

    fun getHandler(): Handler = handler

    fun getThread(): HandlerThread = thread

    fun getLooper(): Looper = thread.looper

    fun getExecutor(): Executor = executor

    fun destroy() {
        if (thread.isAlive) {
            thread.interrupt()
            thread.quit()
        }
        cache.remove(name)
    }

    companion object {
        private const val FALLBACK_NAME = "FallbackCameraThread"
        private val cache = ConcurrentHashMap<String, WeakReference<WorkerHandler>>(4)
        private var fallbackHandler: WorkerHandler? = null

        fun get(name: String): WorkerHandler {
            cache[name]?.get()?.let { workerHandler ->
                if (workerHandler.thread.isAlive && !workerHandler.thread.isInterrupted) {
                    return workerHandler
                }
                workerHandler.destroy()
                cache.remove(name)
            }

            val newHandler = WorkerHandler(name)
            cache[name] = WeakReference(newHandler)
            return newHandler
        }

        fun get(): WorkerHandler {
            val handler = get(FALLBACK_NAME)
            fallbackHandler = handler
            return handler
        }

        fun execute(runnable: Runnable) {
            get().post(runnable)
        }

        fun destroyAll() {
            cache.keys.forEach { key ->
                cache[key]?.get()?.destroy()
                cache[key]?.clear()
            }
            cache.clear()
        }
    }
}

