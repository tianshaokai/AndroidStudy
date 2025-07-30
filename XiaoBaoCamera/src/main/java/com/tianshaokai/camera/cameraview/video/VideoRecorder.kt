package com.tianshaokai.camera.cameraview.video

import com.tianshaokai.camera.cameraview.VideoResult

abstract class VideoRecorder(private val listener: VideoResultListener) {

    companion object {
//        private val LOG = CameraLogger.create(VideoRecorder::class.java.simpleName)
        private const val STATE_IDLE = 0
        private const val STATE_RECORDING = 1
        private const val STATE_STOPPING = 2
        private const val TAG = "VideoRecorder"
    }

    protected var error: Exception? = null
    protected var result: VideoResult.Stub? = null
    private val stateLock = Any()
    private var state = STATE_IDLE

    interface VideoResultListener {
        fun onVideoRecordingEnd()
        fun onVideoRecordingStart()
        fun onVideoResult(result: VideoResult.Stub?, error: Exception?)
    }

    protected open fun onDispatchResult() {}

    protected abstract fun onStart()

    protected abstract fun onStop(isCameraShutdown: Boolean)

    fun start(stub: VideoResult.Stub) {
        synchronized(stateLock) {
            if (state != STATE_IDLE) {
//                LOG.e("start:", "called twice, or while stopping! Ignoring. state: $state")
                return
            }
//            LOG.i("start:", "Changed state to STATE_RECORDING")
            state = STATE_RECORDING
            result = stub
            onStart()
        }
    }

    fun stop(isCameraShutdown: Boolean) {
        synchronized(stateLock) {
            if (state == STATE_IDLE) {
//                LOG.e("stop:", "called twice, or called before start! Ignoring. isCameraShutdown: $isCameraShutdown")
                return
            }
//            LOG.i("stop:", "Changed state to STATE_STOPPING")
            state = STATE_STOPPING
            onStop(isCameraShutdown)
        }
    }

    fun isRecording(): Boolean {
        synchronized(stateLock) {
            return state != STATE_IDLE
        }
    }

    protected fun dispatchResult() {
        synchronized(stateLock) {
            if (!isRecording()) {
//                LOG.w("dispatchResult:", "Called, but not recording! Aborting.")
                return
            }
//            LOG.i("dispatchResult:", "Changed state to STATE_IDLE.")
            state = STATE_IDLE
            onDispatchResult()
//            LOG.i("dispatchResult:", "About to dispatch result: $result, $error")
            listener.onVideoResult(result, error)
            result = null
            error = null
        }
    }

    protected fun dispatchVideoRecordingStart() {
//        LOG.i("dispatchVideoRecordingStart:", "About to dispatch.")
        listener.onVideoRecordingStart()
    }

    protected fun dispatchVideoRecordingEnd() {
//        LOG.i("dispatchVideoRecordingEnd:", "About to dispatch.")
        listener.onVideoRecordingEnd()
    }
}