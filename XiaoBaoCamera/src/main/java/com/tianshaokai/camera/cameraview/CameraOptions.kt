package com.tianshaokai.camera.cameraview

import com.tianshaokai.camera.cameraview.controls.Audio
import com.tianshaokai.camera.cameraview.controls.Control
import com.tianshaokai.camera.cameraview.controls.Engine
import com.tianshaokai.camera.cameraview.controls.Facing
import com.tianshaokai.camera.cameraview.controls.Flash
import com.tianshaokai.camera.cameraview.controls.Grid
import com.tianshaokai.camera.cameraview.controls.Hdr
import com.tianshaokai.camera.cameraview.controls.Mode
import com.tianshaokai.camera.cameraview.controls.PictureFormat
import com.tianshaokai.camera.cameraview.controls.Preview
import com.tianshaokai.camera.cameraview.controls.VideoCodec
import com.tianshaokai.camera.cameraview.controls.WhiteBalance
import com.tianshaokai.camera.cameraview.gesture.GestureAction
import com.tianshaokai.camera.cameraview.size.AspectRatio
import com.tianshaokai.camera.cameraview.size.Size

abstract class CameraOptions {

    protected var autoFocusSupported: Boolean = false
    protected var exposureCorrectionMaxValue: Float = 0f
    protected var exposureCorrectionMinValue: Float = 0f
    protected var exposureCorrectionSupported: Boolean = false
    protected var previewFrameRateMaxValue: Float = 0f
    protected var previewFrameRateMinValue: Float = 0f
    protected var zoomSupported: Boolean = false

    protected val supportedWhiteBalance: MutableSet<WhiteBalance> = HashSet(5)
    protected val supportedFacing: MutableSet<Facing> = HashSet(2)
    protected val supportedFlash: MutableSet<Flash> = HashSet(4)
    protected val supportedHdr: MutableSet<Hdr> = HashSet(2)
    protected val supportedPictureSizes: MutableSet<Size> = HashSet(15)
    protected val supportedVideoSizes: MutableSet<Size> = HashSet(5)
    protected val supportedPictureAspectRatio: MutableSet<AspectRatio> = HashSet(4)
    protected val supportedVideoAspectRatio: MutableSet<AspectRatio> = HashSet(3)
    protected val supportedPictureFormats: MutableSet<PictureFormat> = HashSet(2)
    protected val supportedFrameProcessingFormats: MutableSet<Int> = HashSet(2)

    fun supports(control: Control): Boolean {
        return getSupportedControls(control::class.java).contains(control)
    }

    fun supports(gestureAction: GestureAction): Boolean {
        return when (gestureAction) {
            GestureAction.AUTO_FOCUS -> isAutoFocusSupported()
            GestureAction.TAKE_PICTURE,
            GestureAction.FILTER_CONTROL_1,
            GestureAction.FILTER_CONTROL_2,
            GestureAction.NONE -> true
            GestureAction.ZOOM -> isZoomSupported()
            GestureAction.EXPOSURE_CORRECTION -> isExposureCorrectionSupported()
        }
    }

    open fun <T : Control> getSupportedControls(cls: Class<T>): Collection<T> {
        return when {
            cls == Audio::class.java -> Audio.values().toList()
            cls == Facing::class.java -> getSupportedFacing()
            cls == Flash::class.java -> getSupportedFlash()
            cls == Grid::class.java -> Grid.values().toList()
            cls == Hdr::class.java -> getSupportedHdr()
            cls == Mode::class.java -> Mode.values().toList()
            cls == VideoCodec::class.java -> VideoCodec.values().toList()
            cls == WhiteBalance::class.java -> getSupportedWhiteBalance()
            cls == Engine::class.java -> Engine.values().toList()
            cls == Preview::class.java -> Preview.values().toList()
            cls == PictureFormat::class.java -> getSupportedPictureFormats()
            else -> emptyList()
        }
    }

    fun getSupportedPictureSizes(): Collection<Size> {
        return supportedPictureSizes.toSet()
    }

    fun getSupportedPictureAspectRatios(): Collection<AspectRatio> {
        return supportedPictureAspectRatio.toSet()
    }

    fun getSupportedVideoSizes(): Collection<Size> {
        return supportedVideoSizes.toSet()
    }

    fun getSupportedVideoAspectRatios(): Collection<AspectRatio> {
        return supportedVideoAspectRatio.toSet()
    }

    fun getSupportedFacing(): Collection<Facing> {
        return supportedFacing.toSet()
    }

    fun getSupportedFlash(): Collection<Flash> {
        return supportedFlash.toSet()
    }

    fun getSupportedWhiteBalance(): Collection<WhiteBalance> {
        return supportedWhiteBalance.toSet()
    }

    fun getSupportedHdr(): Collection<Hdr> {
        return supportedHdr.toSet()
    }

    fun getSupportedPictureFormats(): Collection<PictureFormat> {
        return supportedPictureFormats.toSet()
    }

    fun getSupportedFrameProcessingFormats(): Collection<Int> {
        return supportedFrameProcessingFormats.toSet()
    }

    fun isZoomSupported(): Boolean {
        return zoomSupported
    }

    fun isAutoFocusSupported(): Boolean {
        return autoFocusSupported
    }

    fun isExposureCorrectionSupported(): Boolean {
        return exposureCorrectionSupported
    }

    fun getExposureCorrectionMinValue(): Float {
        return exposureCorrectionMinValue
    }

    fun getExposureCorrectionMaxValue(): Float {
        return exposureCorrectionMaxValue
    }

    fun getPreviewFrameRateMinValue(): Float {
        return previewFrameRateMinValue
    }

    fun getPreviewFrameRateMaxValue(): Float {
        return previewFrameRateMaxValue
    }
}