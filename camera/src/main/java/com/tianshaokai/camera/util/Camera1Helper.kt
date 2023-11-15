package com.tianshaokai.camera.util

import android.R.attr.rotation
import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import com.tianshaokai.framework.util.DisplayUtils


/**
 * Camera1 帮助类
 * 主要设置一些 Camera1 公共参数
 *
 */
class Camera1Helper(val context: Context, val mCamera: Camera?) {

    private val TAG = "Camera1Helper"

    /**
     * 设置相机预览 尺寸
     */
    fun setCameraPreviewSize() {
        mCamera?.let {
            val parameters = mCamera.parameters
            val supportedPreviewSizes = parameters.supportedPreviewSizes
            supportedPreviewSizes?.forEach {
                Log.d(TAG, "支持的摄像头预览尺寸 width: ${it.width} height: ${it.height}")
            }


            //设置相机预览尺寸
            parameters.setPreviewSize(1920, 1080)
        }
    }

    fun getOptimalSize(sizes: List<Camera.Size>, screenW: Int, screenH: Int) {
        val orientation = DisplayUtils.getScreenOrientation(context)

        var targetRatio = -1.0
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            targetRatio = (screenH / screenW).toDouble()
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            targetRatio = (screenW / screenH).toDouble()
        }

        val targetHeight: Int = screenW.coerceAtMost(screenH)


    }

    /**
     * 设置 预览方向
     */
    fun setDisplayOrientation() {
       val orientation = DisplayUtils.getScreenOrientation(context)

        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
//        if (mCameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            result = (mCameraInfo.orientation + degrees) % 360
//            result = (360 - result) % 360 // compensate the mirror
//        } else {  // back-facing
            result = (0 - degrees + 360) % 360
//        }
        mCamera?.setDisplayOrientation(result)

    }
}