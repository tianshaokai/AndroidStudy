package com.tianshaokai.camera.cameraview.engin

abstract class CameraEngine(
    private val callback: Callback
) : CameraPreview.SurfaceCallback, PictureRecorder.PictureResultListener, VideoRecorder.VideoResultListener {

    companion object {
        private const val DESTROY_RETRIES = 2
        protected val LOG = CameraLogger.create(CameraEngine::class.java.simpleName)
        protected const val TAG = "CameraEngine"
    }

    private val orchestrator = CameraStateOrchestrator(object : CameraOrchestrator.Callback {
        override fun getJobWorker(str: String): WorkerHandler {
            return handler
        }

        override fun handleJobException(str: String, exc: Exception) {
            handleException(exc, false)
        }
    })

    private var handler: WorkerHandler? = null
    private val crashHandler = Handler(Looper.getMainLooper())

    init {
        recreateHandler(false)
    }

    protected fun getCallback(): Callback = callback

    protected fun getOrchestrator(): CameraStateOrchestrator = orchestrator

    private inner class CrashExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(thread: Thread, throwable: Throwable) {
            handleException(throwable, true)
        }
    }

    private class NoOpExceptionHandler : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(thread: Thread, throwable: Throwable) {
            LOG.w("EXCEPTION:", "In the NoOpExceptionHandler, probably while destroying.", "Thread:", thread, "Error:", throwable)
        }
    }

    private fun handleException(throwable: Throwable, unrecoverable: Boolean) {
        if (unrecoverable) {
            LOG.e("EXCEPTION:", "Handler thread is gone. Replacing.")
            recreateHandler(false)
        }
        LOG.e("EXCEPTION:", "Scheduling on the crash handler...")
        crashHandler.post {
            if (throwable is CameraException) {
                if (throwable.isUnrecoverable) {
                    LOG.e("EXCEPTION:", "Got CameraException. Since it is unrecoverable, executing destroy(false).")
                    destroy(false)
                }
                LOG.e("EXCEPTION:", "Got CameraException. Dispatching to callback.")
                callback.dispatchError(throwable)
            } else {
                LOG.e("EXCEPTION:", "Unexpected error! Executing destroy(true).")
                destroy(true)
                LOG.e("EXCEPTION:", "Unexpected error! Throwing.")
                throw RuntimeException(throwable)
            }
        }
    }

    private fun recreateHandler(resetOrchestrator: Boolean) {
        handler?.destroy()
        handler = WorkerHandler.get("CameraViewEngine").apply {
            thread.uncaughtExceptionHandler = CrashExceptionHandler()
        }
        if (resetOrchestrator) {
            orchestrator.reset()
        }
    }

    fun getState(): CameraState = orchestrator.currentState

    fun getTargetState(): CameraState = orchestrator.targetState

    fun isChangingState(): Boolean = orchestrator.hasPendingStateChange()

    fun destroy(unrecoverable: Boolean) {
        destroy(unrecoverable, 0)
    }

    private fun destroy(unrecoverable: Boolean, depth: Int) {
        LOG.i("DESTROY:", "state:", getState(), "thread:", Thread.currentThread(), "depth:", depth, "unrecoverably:", unrecoverable)
        if (unrecoverable) {
            handler?.thread?.uncaughtExceptionHandler = NoOpExceptionHandler()
        }
        val countDownLatch = CountDownLatch(1)
        stop(true).addOnCompleteListener(handler?.executor) {
            countDownLatch.countDown()
        }
        try {
            if (!countDownLatch.await(6, TimeUnit.SECONDS)) {
                LOG.e("DESTROY: Could not destroy synchronously after 6 seconds.", "Current thread:", Thread.currentThread(), "Handler thread:", handler?.thread)
                val nextDepth = depth + 1
                if (nextDepth < DESTROY_RETRIES) {
                    recreateHandler(true)
                    LOG.e("DESTROY: Trying again on thread:", handler?.thread)
                    destroy(unrecoverable, nextDepth)
                } else {
                    LOG.w("DESTROY: Giving up because DESTROY_RETRIES was reached.")
                }
            }
        } catch (e: InterruptedException) {
            // Handle interruption
        }
    }

    fun restart() {
        LOG.i("RESTART:", "scheduled. State:", getState())
        stop(false)
        start()
    }

    fun start(): Task<Void> {
        LOG.i("START:", "scheduled. State:", getState())
        val taskStartEngine = startEngine()
        startBind()
        startPreview()
        return taskStartEngine
    }

    fun stop(unrecoverable: Boolean): Task<Void> {
        LOG.i("STOP:", "scheduled. State:", getState())
        stopPreview(unrecoverable)
        stopBind(unrecoverable)
        return stopEngine(unrecoverable)
    }

    protected fun restartBind(): Task<Void> {
        LOG.i("RESTART BIND:", "scheduled. State:", getState())
        stopPreview(false)
        stopBind(false)
        startBind()
        return startPreview()
    }

    protected fun restartPreview(): Task<Void> {
        LOG.i("RESTART PREVIEW:", "scheduled. State:", getState())
        stopPreview(false)
        return startPreview()
    }

    private fun startEngine(): Task<Void> {
        return orchestrator.scheduleStateChange(CameraState.OFF, CameraState.ENGINE, true) {
            if (!collectCameraInfo(facing)) {
                LOG.e("onStartEngine:", "No camera available for facing", facing)
                throw CameraException(6)
            }
            onStartEngine()
        }.onSuccessTask {
            it?.let {
                callback.dispatchOnCameraOpened(it)
                Tasks.forResult(null)
            } ?: throw RuntimeException("Null options!")
        }
    }

    private fun stopEngine(unrecoverable: Boolean): Task<Void> {
        return orchestrator.scheduleStateChange(CameraState.ENGINE, CameraState.OFF, !unrecoverable) {
            onStopEngine()
        }.addOnSuccessListener {
            callback.dispatchOnCameraClosed()
        }
    }

    private fun startBind(): Task<Void> {
        return orchestrator.scheduleStateChange(CameraState.ENGINE, CameraState.BIND, true) {
            if (preview?.hasSurface() == true) {
                onStartBind()
            } else {
                Tasks.forCanceled()
            }
        }
    }

    private fun stopBind(unrecoverable: Boolean): Task<Void> {
        return orchestrator.scheduleStateChange(CameraState.BIND, CameraState.ENGINE, !unrecoverable) {
            onStopBind()
        }
    }

    private fun startPreview(): Task<Void> {
        return orchestrator.scheduleStateChange(CameraState.BIND, CameraState.PREVIEW, true) {
            onStartPreview()
        }
    }

    private fun stopPreview(unrecoverable: Boolean): Task<Void> {
        return orchestrator.scheduleStateChange(CameraState.PREVIEW, CameraState.BIND, !unrecoverable) {
            onStopPreview()
        }
    }

    override fun onSurfaceAvailable() {
        LOG.i("onSurfaceAvailable:", "Size is", preview?.surfaceSize)
        startBind()
        startPreview()
    }

    override fun onSurfaceDestroyed() {
        LOG.i("onSurfaceDestroyed")
        stopPreview(false)
        stopBind(false)
    }

    // Abstract methods
    protected abstract fun collectCameraInfo(facing: Facing): Boolean
    abstract fun onStartEngine(): Task<CameraOptions>
    abstract fun onStopEngine(): Task<Void>
    abstract fun onStartBind(): Task<Void>
    abstract fun onStopBind(): Task<Void>
    abstract fun onStartPreview(): Task<Void>
    abstract fun onStopPreview(): Task<Void>
    abstract val facing: Facing
    abstract val preview: CameraPreview?

    interface Callback {
        fun dispatchError(cameraException: CameraException)
        fun dispatchOnCameraClosed()
        fun dispatchOnCameraOpened(cameraOptions: CameraOptions)
    }
}
