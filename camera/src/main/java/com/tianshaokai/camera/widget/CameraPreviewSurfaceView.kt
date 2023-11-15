package com.tianshaokai.camera.widget

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.tianshaokai.camera.util.Camera1Helper
import java.io.IOException

class CameraPreviewSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val TAG = "CameraPreviewSurface"

    //默认打开后置摄像头
    private val mCameraFacingBack = Camera.CameraInfo.CAMERA_FACING_BACK

    private var camera1Helper: Camera1Helper? = null

//    var mHolder: SurfaceHolder? = null
    var mCamera: Camera? = null

    fun startCameraPreview() {
//        mHolder = holder
        holder.addCallback(this)
    }

    private fun startPreview(surfaceHolder: SurfaceHolder) {
        try {
            mCamera?.setPreviewDisplay(surfaceHolder)
            mCamera?.startPreview()
        } catch (e: IOException) {
            Log.e(TAG, "预览失败 ${e.message}")
            mCamera?.release();
        }
    }

    private fun stopPreview() {
        mCamera?.stopPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            mCamera = Camera.open(mCameraFacingBack)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (camera1Helper == null) {
            camera1Helper = Camera1Helper(context, mCamera)
        }

        camera1Helper?.setCameraPreviewSize()
        camera1Helper?.setDisplayOrientation()

        startPreview(holder)

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        mCamera?.parameters?.run {
//            this.setPreviewSize(width, height)
//            mCamera?.parameters = this
//            startPreview(holder)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopPreview()
    }


}