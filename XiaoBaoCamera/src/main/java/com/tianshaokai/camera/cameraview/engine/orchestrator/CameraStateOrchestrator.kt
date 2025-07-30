package com.tianshaokai.camera.cameraview.engine.orchestrator

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.tianshaokai.camera.cameraview.LOG
import java.util.concurrent.Callable

class CameraStateOrchestrator(callback: Callback) : CameraOrchestrator(callback) {

    private var currentState: CameraState = CameraState.OFF
    private var targetState: CameraState = CameraState.OFF
    private var stateChangeCount: Int = 0

    fun getCurrentState(): CameraState = currentState

    fun getTargetState(): CameraState = targetState

    fun hasPendingStateChange(): Boolean {
        synchronized(lock) {
            for (token in jobs) {
                if ((token.name.contains(" >> ") || token.name.contains(" << ")) && !token.task.isComplete) {
                    return true
                }
            }
            return false
        }
    }

    fun <T> scheduleStateChange(
        fromState: CameraState,
        toState: CameraState,
        handleExceptions: Boolean,
        callable: Callable<Task<T>>
    ): Task<T> {
        val changeCount = ++stateChangeCount
        targetState = toState
        val isReversing = !toState.isAtLeast(fromState)
        val taskName = if (isReversing) {
            "${fromState.name} << ${toState.name}"
        } else {
            "${fromState.name} >> ${toState.name}"
        }

        return schedule(taskName, handleExceptions, object : Callable<Task<T>> {
            override fun call(): Task<T> {
                if (getCurrentState() != fromState) {
                    LOG.w(taskName, "- State mismatch, aborting. current: $currentState, from: $fromState, to: $toState")
                    return Tasks.forCanceled()
                }

                return callable.call().continueWithTask(callback.getJobWorker(taskName).getExecutor(), Continuation<T, Task<T>> { task ->
                        if (task.isSuccessful || isReversing) {
                            currentState = toState
                        }
                        task
                    })
            }
        }).addOnCompleteListener {
            if (changeCount == stateChangeCount) {
                targetState = currentState
            }
        }
    }

    fun scheduleStateful(name: String, requiredState: CameraState, runnable: Runnable): Task<Void> {
        return schedule(name, true, Runnable {
            if (getCurrentState().isAtLeast(requiredState)) {
                runnable.run()
            }
        })
    }

    fun scheduleStatefulDelayed(name: String, requiredState: CameraState, delayMillis: Long, runnable: Runnable) {
        scheduleDelayed(name, delayMillis, Runnable {
            if (getCurrentState().isAtLeast(requiredState)) {
                runnable.run()
            }
        })
    }
}
