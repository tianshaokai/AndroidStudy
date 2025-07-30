package com.tianshaokai.camera.cameraview.engine

import android.location.Location
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
import com.tianshaokai.camera.cameraview.engine.orchestrator.CameraState
import com.tianshaokai.camera.cameraview.frame.FrameManager
import com.tianshaokai.camera.cameraview.overlay.Overlay
import com.tianshaokai.camera.cameraview.pictrue.PictureRecorder
import com.tianshaokai.camera.cameraview.preview.CameraPreview
import com.tianshaokai.camera.cameraview.size.AspectRatio
import com.tianshaokai.camera.cameraview.size.Size
import com.tianshaokai.camera.cameraview.size.SizeSelector
import com.tianshaokai.camera.cameraview.size.SizeSelectors
import com.tianshaokai.camera.cameraview.video.VideoRecorder
import java.io.File
import java.io.FileDescriptor

abstract class CameraBaseEngine(callback: CameraEngine.Callback) : CameraEngine(callback) {

    private val angles = Angles()
    private var audio: Audio? = null
    private var audioBitRate: Int = 0
    private var autoFocusResetDelayMillis: Long = 0
    protected var cameraOptions: CameraOptions? = null
    protected var captureSize: Size? = null
    protected var exposureCorrectionTask: Task<Void> = Tasks.forResult(null)
    protected var exposureCorrectionValue: Float = 0f
    private var facing: Facing? = null
    protected var flash: Flash? = null
    protected var flashTask: Task<Void> = Tasks.forResult(null)
    private var frameManager: FrameManager? = null
    protected var frameProcessingFormat: Int = 0
    private var frameProcessingMaxHeight: Int = 0
    private var frameProcessingMaxWidth: Int = 0
    private var frameProcessingPoolSize: Int = 0
    protected var frameProcessingSize: Size? = null
    protected var hasFrameProcessors: Boolean = false
    protected var hdr: Hdr? = null
    protected var hdrTask: Task<Void> = Tasks.forResult(null)
    protected var location: Location? = null
    protected var locationTask: Task<Void> = Tasks.forResult(null)
    private var mode: Mode? = null
    private var overlay: Overlay? = null
    protected var pictureFormat: PictureFormat? = null
    protected var pictureMetering: Boolean = false
    protected var pictureRecorder: PictureRecorder? = null
    private var pictureSizeSelector: SizeSelector? = null
    protected var pictureSnapshotMetering: Boolean = false
    protected var playSounds: Boolean = false
    protected var playSoundsTask: Task<Void> = Tasks.forResult(null)
    protected var preview: CameraPreview? = null
    protected var previewFrameRate: Float = 0f
    private var previewFrameRateExact: Boolean = false
    protected var previewFrameRateTask: Task<Void> = Tasks.forResult(null)
    protected var previewStreamSize: Size? = null
    private var previewStreamSizeSelector: SizeSelector? = null
    private var snapshotMaxHeight: Int = 0
    private var snapshotMaxWidth: Int = 0
    private var videoBitRate: Int = 0
    protected var videoCodec: VideoCodec? = null
    private var videoMaxDuration: Int = 0
    private var videoMaxSize: Long = 0
    protected var videoRecorder: VideoRecorder? = null
    private var videoSizeSelector: SizeSelector? = null
    protected var whiteBalance: WhiteBalance? = null
    protected var whiteBalanceTask: Task<Void> = Tasks.forResult(null)
    protected var zoomTask: Task<Void> = Tasks.forResult(null)
    protected var zoomValue: Float = 0f

    protected abstract fun getFrameProcessingAvailableSizes(): List<Size>

    protected abstract fun getPreviewStreamAvailableSizes(): List<Size>

    protected abstract fun instantiateFrameManager(poolSize: Int): FrameManager

    protected abstract fun onPreviewStreamSizeChanged()

    protected abstract fun onTakePicture(stub: PictureResult.Stub, metering: Boolean)

    protected abstract fun onTakePictureSnapshot(stub: PictureResult.Stub, aspectRatio: AspectRatio, metering: Boolean)

    protected abstract fun onTakeVideo(stub: VideoResult.Stub)

    protected abstract fun onTakeVideoSnapshot(stub: VideoResult.Stub, aspectRatio: AspectRatio)


    init {
        // Initialize tasks to default completed state
        zoomTask = Tasks.forResult(null)
        exposureCorrectionTask = Tasks.forResult(null)
        flashTask = Tasks.forResult(null)
        whiteBalanceTask = Tasks.forResult(null)
        hdrTask = Tasks.forResult(null)
        locationTask = Tasks.forResult(null)
        playSoundsTask = Tasks.forResult(null)
        previewFrameRateTask = Tasks.forResult(null)
    }

    override fun getAngles(): Angles = angles

    override fun getFrameManager(): FrameManager {
        if (frameManager == null) {
            frameManager = instantiateFrameManager(frameProcessingPoolSize)
        }
        return frameManager!!
    }

    override fun getCameraOptions(): CameraOptions? = cameraOptions

    override fun setPreview(cameraPreview: CameraPreview) {
        preview?.setSurfaceCallback(null)
        preview = cameraPreview
        cameraPreview.setSurfaceCallback(this)
    }

    override fun getPreview(): CameraPreview? = preview

    override fun setOverlay(overlay: Overlay) {
        this.overlay = overlay
    }

    override fun getOverlay(): Overlay? = overlay

    override fun setPreviewStreamSizeSelector(sizeSelector: SizeSelector) {
        previewStreamSizeSelector = sizeSelector
    }

    override fun getPreviewStreamSizeSelector(): SizeSelector? = previewStreamSizeSelector

    override fun setPictureSizeSelector(sizeSelector: SizeSelector) {
        pictureSizeSelector = sizeSelector
    }

    override fun getPictureSizeSelector(): SizeSelector? = pictureSizeSelector

    override fun setVideoSizeSelector(sizeSelector: SizeSelector) {
        videoSizeSelector = sizeSelector
    }

    override fun getVideoSizeSelector(): SizeSelector? = videoSizeSelector

    override fun setVideoMaxSize(maxSize: Long) {
        videoMaxSize = maxSize
    }

    override fun getVideoMaxSize(): Long = videoMaxSize

    override fun setVideoMaxDuration(maxDuration: Int) {
        videoMaxDuration = maxDuration
    }

    override fun getVideoMaxDuration(): Int = videoMaxDuration

    override fun setVideoCodec(videoCodec: VideoCodec) {
        this.videoCodec = videoCodec
    }

    override fun getVideoCodec(): VideoCodec? = videoCodec

    override fun setVideoBitRate(bitRate: Int) {
        videoBitRate = bitRate
    }

    override fun getVideoBitRate(): Int = videoBitRate

    override fun setAudioBitRate(bitRate: Int) {
        audioBitRate = bitRate
    }

    override fun getAudioBitRate(): Int = audioBitRate

    override fun setSnapshotMaxWidth(maxWidth: Int) {
        snapshotMaxWidth = maxWidth
    }

    override fun getSnapshotMaxWidth(): Int = snapshotMaxWidth

    override fun setSnapshotMaxHeight(maxHeight: Int) {
        snapshotMaxHeight = maxHeight
    }

    override fun getSnapshotMaxHeight(): Int = snapshotMaxHeight

    override fun setFrameProcessingMaxWidth(maxWidth: Int) {
        frameProcessingMaxWidth = maxWidth
    }

    override fun getFrameProcessingMaxWidth(): Int = frameProcessingMaxWidth

    override fun setFrameProcessingMaxHeight(maxHeight: Int) {
        frameProcessingMaxHeight = maxHeight
    }

    override fun getFrameProcessingMaxHeight(): Int = frameProcessingMaxHeight

    override fun setFrameProcessingPoolSize(poolSize: Int) {
        frameProcessingPoolSize = poolSize
    }

    override fun getFrameProcessingPoolSize(): Int = frameProcessingPoolSize

    override fun setAutoFocusResetDelay(delay: Long) {
        autoFocusResetDelayMillis = delay
    }

    override fun getAutoFocusResetDelay(): Long = autoFocusResetDelayMillis

    protected fun shouldResetAutoFocus(): Boolean {
        return autoFocusResetDelayMillis > 0 && autoFocusResetDelayMillis != Long.MAX_VALUE
    }

    override fun setFacing(facing: Facing) {
        val previousFacing = this.facing
        if (facing != previousFacing) {
            this.facing = facing
            getOrchestrator().scheduleStateful("facing", CameraState.ENGINE) {
                if (collectCameraInfo(facing)) {
                    restart()
                } else {
                    this.facing = previousFacing
                }
            }
        }
    }

    override fun getFacing(): Facing? = facing

    override fun setAudio(audio: Audio) {
        if (this.audio != audio) {
            if (isTakingVideo()) {
                LOG.w("Audio setting was changed while recording. Changes will take place starting from next video")
            }
            this.audio = audio
        }
    }

    override fun getAudio(): Audio? = audio

    override fun setMode(mode: Mode) {
        if (this.mode != mode) {
            this.mode = mode
            getOrchestrator().scheduleStateful("mode", CameraState.ENGINE) {
                restart()
            }
        }
    }

    override fun getMode(): Mode? = mode

    override fun getZoomValue(): Float = zoomValue

    override fun getExposureCorrectionValue(): Float = exposureCorrectionValue

    override fun getFlash(): Flash? = flash

    override fun getWhiteBalance(): WhiteBalance? = whiteBalance

    override fun getHdr(): Hdr? = hdr

    override fun getLocation(): Location? = location

    override fun getPictureFormat(): PictureFormat? = pictureFormat

    override fun setPreviewFrameRateExact(exact: Boolean) {
        previewFrameRateExact = exact
    }

    override fun getPreviewFrameRateExact(): Boolean = previewFrameRateExact

    override fun getPreviewFrameRate(): Float = previewFrameRate

    override fun hasFrameProcessors(): Boolean = hasFrameProcessors

    override fun setPictureMetering(metering: Boolean) {
        pictureMetering = metering
    }

    override fun getPictureMetering(): Boolean = pictureMetering

    override fun setPictureSnapshotMetering(metering: Boolean) {
        pictureSnapshotMetering = metering
    }

    override fun getPictureSnapshotMetering(): Boolean = pictureSnapshotMetering

    override fun isTakingPicture(): Boolean = pictureRecorder != null

    override fun isTakingVideo(): Boolean {
        return videoRecorder?.isRecording() ?: false
    }

    override fun takePicture(stub: PictureResult.Stub) {
        val metering = pictureMetering
        getOrchestrator().scheduleStateful("take picture", CameraState.BIND) {
            LOG.i("takePicture:", "running. isTakingPicture: ${isTakingPicture()}")
            if (isTakingPicture() || mode == Mode.VIDEO) {
                return@scheduleStateful
            }
            stub.isSnapshot = false
            stub.location = location
            stub.facing = facing
            stub.format = pictureFormat
            onTakePicture(stub, metering)
        }
    }

    override fun takePictureSnapshot(stub: PictureResult.Stub) {
        val metering = pictureSnapshotMetering
        getOrchestrator().scheduleStateful("take picture snapshot", CameraState.BIND) {
            LOG.i("takePictureSnapshot:", "running. isTakingPicture: ${isTakingPicture()}")
            if (isTakingPicture()) {
                return@scheduleStateful
            }
            stub.location = location
            stub.isSnapshot = true
            stub.facing = facing
            stub.format = PictureFormat.JPEG
            onTakePictureSnapshot(stub, AspectRatio.of(getPreviewSurfaceSize(Reference.OUTPUT)), metering)
        }
    }

    override fun onPictureShutter(isShutter: Boolean) {
        getCallback().onShutter(!isShutter)
    }

    fun onPictureResult(stub: PictureResult.Stub?, exception: Exception?) {
        pictureRecorder = null
        if (stub != null) {
            getCallback().dispatchOnPictureTaken(stub)
        } else {
            LOG.e("onPictureResult", "result is null: something went wrong.", exception)
            getCallback().dispatchError(CameraException(exception, 4))
        }
    }

    override fun isTakingVideo(): Boolean {
        return videoRecorder?.isRecording() ?: false
    }

    override fun takeVideo(stub: VideoResult.Stub, file: File?, fileDescriptor: FileDescriptor?) {
        getOrchestrator().scheduleStateful("take video", CameraState.BIND) {
            LOG.i("takeVideo:", "running. isTakingVideo: ${isTakingVideo()}")
            if (isTakingVideo()) {
                return@scheduleStateful
            }
            if (mode == Mode.PICTURE) {
                throw IllegalStateException("Can't record video while in PICTURE mode")
            }
            when {
                file != null -> stub.file = file
                fileDescriptor != null -> stub.fileDescriptor = fileDescriptor
                else -> throw IllegalStateException("file and fileDescriptor are both null.")
            }
            stub.isSnapshot = false
            stub.videoCodec = videoCodec
            stub.location = location
            stub.facing = facing
            stub.audio = audio
            stub.maxSize = videoMaxSize
            stub.maxDuration = videoMaxDuration
            stub.videoBitRate = videoBitRate
            stub.audioBitRate = audioBitRate
            onTakeVideo(stub)
        }
    }

    override fun takeVideoSnapshot(stub: VideoResult.Stub, file: File) {
        getOrchestrator().scheduleStateful("take video snapshot", CameraState.BIND) {
            LOG.i("takeVideoSnapshot:", "running. isTakingVideo: ${isTakingVideo()}")
            stub.file = file
            stub.isSnapshot = true
            stub.videoCodec = videoCodec
            stub.location = location
            stub.facing = facing
            stub.videoBitRate = videoBitRate
            stub.audioBitRate = audioBitRate
            stub.audio = audio
            stub.maxSize = videoMaxSize
            stub.maxDuration = videoMaxDuration
            onTakeVideoSnapshot(stub, AspectRatio.of(getPreviewSurfaceSize(Reference.OUTPUT)))
        }
    }

    override fun stopVideo() {
        getOrchestrator().schedule("stop video", true) {
            LOG.i("stopVideo", "running. isTakingVideo? ${isTakingVideo()}")
            onStopVideo()
        }
    }

    protected open fun onStopVideo() {
        videoRecorder?.stop(false)
    }

    fun onVideoResult(stub: VideoResult.Stub?, exception: Exception?) {
        videoRecorder = null
        if (stub != null) {
            getCallback().dispatchOnVideoTaken(stub)
        } else {
            LOG.e("onVideoResult", "result is null: something went wrong.", exception)
            getCallback().dispatchError(CameraException(exception, 5))
        }
    }

    override fun onVideoRecordingStart() {
        getCallback().dispatchOnVideoRecordingStart()
    }

    fun onVideoRecordingEnd() {
        getCallback().dispatchOnVideoRecordingEnd()
    }

    override fun onSurfaceChanged() {
        LOG.i("onSurfaceChanged:", "Size is ${getPreviewSurfaceSize(Reference.VIEW)}")
        getOrchestrator().scheduleStateful("surface changed", CameraState.BIND) {
            val newPreviewSize = computePreviewStreamSize()
            if (newPreviewSize == previewStreamSize) {
                LOG.i("onSurfaceChanged:", "The computed preview size is identical. No op.")
                return@scheduleStateful
            }
            LOG.i("onSurfaceChanged:", "Computed a new preview size. Calling onPreviewStreamSizeChanged().")
            previewStreamSize = newPreviewSize
            onPreviewStreamSizeChanged()
        }
    }

    override fun getPictureSize(reference: Reference): Size? {
        val size = captureSize
        if (size == null || mode == Mode.VIDEO) {
            return null
        }
        return if (angles.flip(Reference.SENSOR, reference)) size.flip() else size
    }

    override fun getVideoSize(reference: Reference): Size? {
        val size = captureSize
        if (size == null || mode == Mode.PICTURE) {
            return null
        }
        return if (angles.flip(Reference.SENSOR, reference)) size.flip() else size
    }

    override fun getPreviewStreamSize(reference: Reference): Size? {
        val size = previewStreamSize
        if (size == null) {
            return null
        }
        return if (angles.flip(Reference.SENSOR, reference)) size.flip() else size
    }

    private fun getPreviewSurfaceSize(reference: Reference): Size? {
        val preview = preview ?: return null
        return if (angles.flip(Reference.VIEW, reference)) preview.surfaceSize.flip() else preview.surfaceSize
    }

    override fun getUncroppedSnapshotSize(reference: Reference): Size? {
        val previewSize = getPreviewStreamSize(reference) ?: return null
        val flip = angles.flip(reference, Reference.VIEW)
        var maxWidth = if (flip) snapshotMaxHeight else snapshotMaxWidth
        var maxHeight = if (flip) snapshotMaxWidth else snapshotMaxHeight
        if (maxWidth <= 0) maxWidth = Int.MAX_VALUE
        if (maxHeight <= 0) maxHeight = Int.MAX_VALUE
        val aspectRatio = AspectRatio.of(previewSize.width, previewSize.height)
        return if (aspectRatio.toFloat() >= AspectRatio.of(maxWidth, maxHeight).toFloat()) {
            Size((maxHeight * aspectRatio.toFloat()).toInt(), maxHeight)
        } else {
            Size(maxWidth, (maxWidth / aspectRatio.toFloat()).toInt())
        }
    }


    protected fun computeCaptureSize(): Size {
        return computeCaptureSize(mode)
    }

    protected fun computeCaptureSize(mode: Mode?): Size {
        val sizeSelector: SizeSelector?
        val supportedSizes: Collection<Size>
        val flip = angles.flip(Reference.SENSOR, Reference.VIEW)

        if (mode == Mode.PICTURE) {
            sizeSelector = pictureSizeSelector
            supportedSizes = cameraOptions?.supportedPictureSizes ?: emptyList()
        } else {
            sizeSelector = videoSizeSelector
            supportedSizes = cameraOptions?.supportedVideoSizes ?: emptyList()
        }

        val combinedSelector = SizeSelectors.or(sizeSelector, SizeSelectors.biggest())
        val sizeList = ArrayList(supportedSizes)
        val selectedSize = combinedSelector.select(sizeList).firstOrNull()
            ?: throw RuntimeException("SizeSelectors must not return Sizes other than those in the input list.")

        LOG.i("computeCaptureSize:", "result: $selectedSize, flip: $flip, mode: $mode")
        return if (flip) selectedSize.flip() else selectedSize
    }

    protected fun computePreviewStreamSize(): Size {
        val availableSizes = getPreviewStreamAvailableSizes()
        val flip = angles.flip(Reference.SENSOR, Reference.VIEW)

        val flippedSizes = availableSizes.map { if (flip) it.flip() else it }
        val previewSurfaceSize = getPreviewSurfaceSize(Reference.VIEW)
            ?: throw IllegalStateException("targetMinSize should not be null here.")

        val targetAspectRatio = AspectRatio.of(captureSize?.width ?: 0, captureSize?.height ?: 0)
        val adjustedAspectRatio = if (flip) targetAspectRatio.flip() else targetAspectRatio

        LOG.i("computePreviewStreamSize:", "targetRatio: $adjustedAspectRatio, targetMinSize: $previewSurfaceSize")

        val sizeSelector = SizeSelectors.or(
            SizeSelectors.and(
                SizeSelectors.aspectRatio(adjustedAspectRatio, 0.0f),
                SizeSelectors.biggest()
            ),
            SizeSelectors.and(
                SizeSelectors.minHeight(previewSurfaceSize.height),
                SizeSelectors.minWidth(previewSurfaceSize.width),
                SizeSelectors.smallest()
            ),
            SizeSelectors.biggest()
        )

        val combinedSelector = previewStreamSizeSelector?.let {
            SizeSelectors.or(it, sizeSelector)
        } ?: sizeSelector

        val selectedSize = combinedSelector.select(flippedSizes).firstOrNull()
            ?: throw RuntimeException("SizeSelectors must not return Sizes other than those in the input list.")

        LOG.i("computePreviewStreamSize:", "result: $selectedSize, flip: $flip")
        return if (flip) selectedSize.flip() else selectedSize
    }

    protected fun computeFrameProcessingSize(): Size {
        val availableSizes = getFrameProcessingAvailableSizes()
        val flip = angles.flip(Reference.SENSOR, Reference.VIEW)

        val flippedSizes = availableSizes.map { if (flip) it.flip() else it }
        val targetAspectRatio = AspectRatio.of(previewStreamSize?.width ?: 0, previewStreamSize?.height ?: 0)
        val adjustedAspectRatio = if (flip) targetAspectRatio.flip() else targetAspectRatio

        var maxWidth = frameProcessingMaxWidth
        var maxHeight = frameProcessingMaxHeight

        if (maxWidth <= 0 || maxWidth == Int.MAX_VALUE) maxWidth = 640
        if (maxHeight <= 0 || maxHeight == Int.MAX_VALUE) maxHeight = 640

        val targetMaxSize = Size(maxWidth, maxHeight)

        LOG.i("computeFrameProcessingSize:", "targetRatio: $adjustedAspectRatio, targetMaxSize: $targetMaxSize")

        val sizeSelector = SizeSelectors.or(
            SizeSelectors.and(
                SizeSelectors.aspectRatio(adjustedAspectRatio, 0.0f),
                SizeSelectors.and(
                    SizeSelectors.maxHeight(targetMaxSize.height),
                    SizeSelectors.maxWidth(targetMaxSize.width),
                    SizeSelectors.biggest()
                )
            ),
            SizeSelectors.smallest()
        )

        val selectedSize = sizeSelector.select(flippedSizes).firstOrNull()
            ?: throw RuntimeException("SizeSelectors must not return Sizes other than those in the input list.")

        LOG.i("computeFrameProcessingSize:", "result: $selectedSize, flip: $flip")
        return if (flip) selectedSize.flip() else selectedSize
    }

}
