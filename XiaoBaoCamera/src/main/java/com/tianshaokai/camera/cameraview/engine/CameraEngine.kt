package com.tianshaokai.camera.cameraview.engine

import android.content.Context
import android.gesture.Gesture
import android.graphics.Matrix
import android.graphics.PointF
import android.location.Location
import android.os.Handler
import android.os.Looper
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.tianshaokai.camera.cameraview.CameraException
import com.tianshaokai.camera.cameraview.CameraOptions
import com.tianshaokai.camera.cameraview.LOG
import com.tianshaokai.camera.cameraview.PictureResult
import com.tianshaokai.camera.cameraview.VideoResult
import com.tianshaokai.camera.cameraview.controls.Audio
import com.tianshaokai.camera.cameraview.controls.Facing
import com.tianshaokai.camera.cameraview.controls.Flash
import com.tianshaokai.camera.cameraview.controls.Hdr
import com.tianshaokai.camera.cameraview.controls.Mode
import com.tianshaokai.camera.cameraview.controls.PictureFormat
import com.tianshaokai.camera.cameraview.controls.VideoCodec
import com.tianshaokai.camera.cameraview.controls.WhiteBalance
import com.tianshaokai.camera.cameraview.engine.offset.Angles
import com.tianshaokai.camera.cameraview.engine.offset.Reference
import com.tianshaokai.camera.cameraview.engine.orchestrator.CameraOrchestrator
import com.tianshaokai.camera.cameraview.engine.orchestrator.CameraState
import com.tianshaokai.camera.cameraview.engine.orchestrator.CameraStateOrchestrator
import com.tianshaokai.camera.cameraview.frame.Frame
import com.tianshaokai.camera.cameraview.frame.FrameManager
import com.tianshaokai.camera.cameraview.internal.WorkerHandler
import com.tianshaokai.camera.cameraview.overlay.Overlay
import com.tianshaokai.camera.cameraview.pictrue.PictureRecorder
import com.tianshaokai.camera.cameraview.preview.CameraPreview
import com.tianshaokai.camera.cameraview.size.Size
import com.tianshaokai.camera.cameraview.size.SizeSelector
import com.tianshaokai.camera.cameraview.video.VideoRecorder
import java.io.File
import java.io.FileDescriptor
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

abstract class CameraEngine(
    private val callback: Callback
) : CameraPreview.SurfaceCallback, PictureRecorder.PictureResultListener, VideoRecorder.VideoResultListener {

    companion object {
        private const val DESTROY_RETRIES = 2
//        private val LOG = CameraLogger.create(CameraEngine::class.java.simpleName)
        private const val TAG = "CameraEngine"
    }

    private var handler: WorkerHandler? = null
    private val orchestrator = CameraStateOrchestrator(object : CameraOrchestrator.Callback {
        override fun getJobWorker(name: String): WorkerHandler {
            return handler!!
        }

        override fun handleJobException(name: String, exception: Exception) {
            handleException(exception, false)
        }
    })
    private val crashHandler = Handler(Looper.getMainLooper())

    interface Callback {
        fun dispatchError(cameraException: CameraException)
        fun dispatchFrame(frame: Frame)
        fun dispatchOnCameraClosed()
        fun dispatchOnCameraOpened(cameraOptions: CameraOptions)
        fun dispatchOnExposureCorrectionChanged(value: Float, bounds: FloatArray, points: Array<PointF>)
        fun dispatchOnFocusEnd(gesture: Gesture, success: Boolean, point: PointF)
        fun dispatchOnFocusStart(gesture: Gesture, point: PointF)
        fun dispatchOnPictureTaken(stub: PictureResult.Stub)
        fun dispatchOnVideoRecordingEnd()
        fun dispatchOnVideoRecordingStart()
        fun dispatchOnVideoTaken(stub: VideoResult.Stub)
        fun dispatchOnZoomChanged(value: Float, points: Array<PointF>)
        fun drawFrame(data: ByteArray, matrix: Matrix)
        fun getContext(): Context
        fun getHeightViewFilter(): Int
        fun getRotationActivity(): Int
        fun getWidthViewFilter(): Int
        fun onCameraPreviewStreamSizeChanged()
        fun onShutter(isShutter: Boolean)
    }

    protected abstract fun collectCameraInfo(facing: Facing): Boolean
    abstract fun getAngles(): Angles
    abstract fun getAudio(): Audio
    abstract fun getAudioBitRate(): Int
    abstract fun getAutoFocusResetDelay(): Long
    abstract fun getCameraOptions(): CameraOptions
    abstract fun getExposureCorrectionValue(): Float
    abstract fun getFacing(): Facing
    abstract fun getFlash(): Flash
    abstract fun getFrameManager(): FrameManager
    abstract fun getFrameProcessingFormat(): Int
    abstract fun getFrameProcessingMaxHeight(): Int
    abstract fun getFrameProcessingMaxWidth(): Int
    abstract fun getFrameProcessingPoolSize(): Int
    abstract fun getHdr(): Hdr
    abstract fun getLocation(): Location
    abstract fun getMode(): Mode
    abstract fun getOverlay(): Overlay
    abstract fun getPictureFormat(): PictureFormat
    abstract fun getPictureMetering(): Boolean
    abstract fun getPictureSize(reference: Reference): Size
    abstract fun getPictureSizeSelector(): SizeSelector
    abstract fun getPictureSnapshotMetering(): Boolean
    abstract fun getPreview(): CameraPreview
    abstract fun getPreviewFrameRate(): Float
    abstract fun getPreviewFrameRateExact(): Boolean
    abstract fun getPreviewStreamSize(reference: Reference): Size
    abstract fun getPreviewStreamSizeSelector(): SizeSelector
    abstract fun getSnapshotMaxHeight(): Int
    abstract fun getSnapshotMaxWidth(): Int
    abstract fun getUncroppedSnapshotSize(reference: Reference): Size
    abstract fun getVideoBitRate(): Int
    abstract fun getVideoCodec(): VideoCodec
    abstract fun getVideoMaxDuration(): Int
    abstract fun getVideoMaxSize(): Long
    abstract fun getVideoSize(reference: Reference): Size
    abstract fun getVideoSizeSelector(): SizeSelector
    abstract fun getWhiteBalance(): WhiteBalance
    abstract fun getZoomValue(): Float
    abstract fun hasFrameProcessors(): Boolean
    abstract fun isTakingPicture(): Boolean
    abstract fun isTakingVideo(): Boolean
    protected abstract fun onStartBind(): Task<Void>
    protected abstract fun onStartEngine(): Task<CameraOptions>
    protected abstract fun onStartPreview(): Task<Void>
    protected abstract fun onStopBind(): Task<Void>
    protected abstract fun onStopEngine(): Task<Void>
    protected abstract fun onStopPreview(): Task<Void>
    abstract fun setAudio(audio: Audio)
    abstract fun setAudioBitRate(bitRate: Int)
    abstract fun setAutoFocusResetDelay(delay: Long)
    abstract fun setExposureCorrection(value: Float, bounds: FloatArray, points: Array<PointF>, notify: Boolean)
    abstract fun setFacing(facing: Facing)
    abstract fun setFlash(flash: Flash)
    abstract fun setFrameProcessingFormat(format: Int)
    abstract fun setFrameProcessingMaxHeight(maxHeight: Int)
    abstract fun setFrameProcessingMaxWidth(maxWidth: Int)
    abstract fun setFrameProcessingPoolSize(poolSize: Int)
    abstract fun setHasFrameProcessors(hasProcessors: Boolean)
    abstract fun setHdr(hdr: Hdr)
    abstract fun setLocation(location: Location)
    abstract fun setMode(mode: Mode)
    abstract fun setOverlay(overlay: Overlay)
    abstract fun setPictureFormat(format: PictureFormat)
    abstract fun setPictureMetering(metering: Boolean)
    abstract fun setPictureSizeSelector(selector: SizeSelector)
    abstract fun setPictureSnapshotMetering(metering: Boolean)
    abstract fun setPlaySounds(playSounds: Boolean)
    abstract fun setPreview(preview: CameraPreview)
    abstract fun setPreviewFrameRate(frameRate: Float)
    abstract fun setPreviewFrameRateExact(exact: Boolean)
    abstract fun setPreviewStreamSizeSelector(selector: SizeSelector)
    abstract fun setSnapshotMaxHeight(maxHeight: Int)
    abstract fun setSnapshotMaxWidth(maxWidth: Int)
    abstract fun setVideoBitRate(bitRate: Int)
    abstract fun setVideoCodec(codec: VideoCodec)
    abstract fun setVideoMaxDuration(maxDuration: Int)
    abstract fun setVideoMaxSize(maxSize: Long)
    abstract fun setVideoSizeSelector(selector: SizeSelector)
    abstract fun setWhiteBalance(whiteBalance: WhiteBalance)
    abstract fun setZoom(value: Float, points: Array<PointF>, notify: Boolean)
    abstract fun startAutoFocus(gesture: Gesture, meteringRegions: MeteringRegions, point: PointF)
    abstract fun stopVideo()
    abstract fun takePicture(stub: PictureResult.Stub)
    abstract fun takePictureSnapshot(stub: PictureResult.Stub)
    abstract fun takeVideo(stub: VideoResult.Stub, file: File, fileDescriptor: FileDescriptor)
    abstract fun takeVideoSnapshot(stub: VideoResult.Stub, file: File)

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
                if (throwable.isUnrecoverable()) {
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

    fun getState(): CameraState = orchestrator.getCurrentState()

    fun getTargetState(): CameraState = orchestrator.getTargetState()

    fun isChangingState(): Boolean = orchestrator.hasPendingStateChange()

    fun destroy(unrecoverable: Boolean) {
        destroy(unrecoverable, 0)
    }

    private fun destroy(unrecoverable: Boolean, depth: Int) {
        LOG.i("DESTROY:", "state:", getState(), "thread:", Thread.currentThread(), "depth:", depth, "unrecoverably:", unrecoverable)
        if (unrecoverable) {
            handler?.thread?.uncaughtExceptionHandler = NoOpExceptionHandler()
        }
        val latch = CountDownLatch(1)
        stop(true).addOnCompleteListener(handler!!.executor) {
            latch.countDown()
        }
        try {
            if (!latch.await(6, TimeUnit.SECONDS)) {
                LOG.e("DESTROY: Could not destroy synchronously after 6 seconds.", "Current thread:", Thread.currentThread(), "Handler thread:", handler?.thread)
                if (depth + 1 < DESTROY_RETRIES) {
                    recreateHandler(true)
                    LOG.e("DESTROY: Trying again on thread:", handler?.thread)
                    destroy(unrecoverable, depth + 1)
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
        val engineTask = startEngine()
        startBind()
        startPreview()
        return engineTask
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
            if (!collectCameraInfo(getFacing())) {
                LOG.e("onStartEngine:", "No camera available for facing", getFacing())
                throw CameraException(CameraException.REASON_NO_CAMERA)
            }
            onStartEngine()
        }.onSuccessTask {
            callback.dispatchOnCameraOpened(it)
            Tasks.forResult(null)
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
            if (getPreview()?.hasSurface() == true) {
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
        LOG.i("onSurfaceAvailable:", "Size is", getPreview().getSurfaceSize())
        startBind()
        startPreview()
    }

    override fun onSurfaceDestroyed() {
        LOG.i("onSurfaceDestroyed")
        stopPreview(false)
        stopBind(false)
    }
}
