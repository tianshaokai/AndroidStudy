package com.tianshaokai.camera.camera1.basic

import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import com.tianshaokai.camera.api.CameraActions
import com.tianshaokai.camera.api.PreviewAction
import com.tianshaokai.camera.camera1.util.Camera1Manager
import com.tianshaokai.camera.type.AspectRatio
import com.tianshaokai.camera.type.CameraFacing
import com.tianshaokai.camera.type.Size
import com.tianshaokai.camera.type.SizeMap
import java.util.SortedSet

class Camera1Engine(val previewAction: PreviewAction) : CameraActions {

    private val TAG = "Camera1Engine"
    private var mCamera: Camera? = null
    private var mCameraParameters: Camera.Parameters? = null
    private var mCameraId = Camera1Manager.getCameraId(CameraFacing.BACK)
    private val mCameraInfo = Camera.CameraInfo()

    private var mAspectRatio: AspectRatio = AspectRatio.of(4, 3)

    private val mPreviewSizes: SizeMap = SizeMap()
    private val mPictureSizes: SizeMap = SizeMap()

    private var mAutoFocus = false

    private fun open(surfaceHolder: SurfaceHolder) {
        kotlin.runCatching {
            Camera.getCameraInfo(mCameraId, mCameraInfo)
            mCamera = Camera.open(mCameraId)
            mCamera?.run {
                mCameraParameters = this.parameters
                setCameraParameter()
                adjustCameraParameters()

                val orientation: Int = Camera1Manager.calcDisplayOrientation(0, mCameraInfo)
                this.setDisplayOrientation(orientation)

                this.setPreviewDisplay(surfaceHolder)
                this.startPreview()
            }
        }.onFailure {

        }
    }

    private fun setCameraParameter() {
        mCameraParameters?.run {
            mPreviewSizes.clear()
            for (size in this.supportedPreviewSizes) {
                Log.d(TAG, "支持的摄像头预览尺寸 width: ${size.width} height: ${size.height}")
                mPreviewSizes.add(Size(size.width, size.height))
            }

            mPictureSizes.clear()
            for (size in this.supportedPictureSizes) {
                Log.d(TAG, "支持的摄像头图片尺寸 width: ${size.width} height: ${size.height}")
                mPictureSizes.add(Size(size.width, size.height))
            }
        }
    }

    private fun adjustCameraParameters() {
        mCameraParameters?.run {
            val previewSizeList = mPreviewSizes.sizes(mAspectRatio)

            previewSizeList?.let {
                val previewSize = chooseOptimalSize(it)
                previewSize?.let {
                    Log.d(TAG, "设置预览尺寸 width: ${previewSize.width} height: ${previewSize.height}")
                    setPreviewSize(previewSize.width, previewSize.height)
                }
            }

            // Always re-apply camera parameters
            // Largest picture size in this ratio
            val pictureSize = mPictureSizes.sizes(mAspectRatio)?.last()
            pictureSize?.let {
                Log.d(TAG, "设置图片尺寸 width: ${pictureSize.width} height: ${pictureSize.height}")
                setPictureSize(pictureSize.width, pictureSize.height)
            }
//            setAutoFocusInternal(mAutoFocus)

            mCamera?.parameters = this
        }
    }

    private fun chooseOptimalSize(sizes: SortedSet<Size>):Size? {
        var desiredWidth: Int
        var desiredHeight: Int

        val surfaceWidth = previewAction.getPreviewWidth()
        val surfaceHeight = previewAction.getPreviewHeight()

        desiredWidth = surfaceWidth
        desiredHeight = surfaceHeight

        var result: Size? = null
        for (size in sizes) { // Iterate from small to large
            if (desiredWidth <= size.width && desiredHeight <= size.height) {
                return size
            }
            result = size
        }
        return result
    }

    private fun setAutoFocusInternal(autoFocus: Boolean): Boolean {
        mAutoFocus = autoFocus
//        if (isCameraOpened()) {
            val modes = mCameraParameters!!.supportedFocusModes
            if (autoFocus && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mCameraParameters!!.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                mCameraParameters!!.focusMode = Camera.Parameters.FOCUS_MODE_FIXED
            } else if (modes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                mCameraParameters!!.focusMode = Camera.Parameters.FOCUS_MODE_INFINITY
            } else {
                mCameraParameters!!.focusMode = modes[0]
            }
            return true
//        } else {
//            return false
//        }
    }


    override fun startPreview(surfaceHolder: SurfaceHolder) {
        open(surfaceHolder)
    }

    override fun stopPreview() {
        mCamera?.stopPreview()
    }

    /**
     * 设置摄像头前后置
     */
    override fun setCameraFacing(cameraFacing: CameraFacing): Camera1Engine {
        this.mCameraId = Camera1Manager.getCameraId(cameraFacing)
        return this
    }


}