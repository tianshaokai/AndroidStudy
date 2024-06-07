package com.tianshaokai.camera.camera1.basic

import android.hardware.Camera
import android.view.SurfaceHolder
import com.tianshaokai.camera.api.CameraActions
import com.tianshaokai.camera.camera1.util.Camera1Manager
import com.tianshaokai.camera.type.CameraFacing

class Camera1Engine : CameraActions {

    private var mCamera: Camera? = null
    private var cameraFacing = Camera1Manager.getCameraId(CameraFacing.BACK)

    private fun open(surfaceHolder: SurfaceHolder) {
        kotlin.runCatching {
            mCamera = Camera.open(cameraFacing)
            mCamera?.run {
                this.setPreviewDisplay(surfaceHolder)
                this.startPreview()
            }
        }.onFailure {

        }
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
        this.cameraFacing = Camera1Manager.getCameraId(cameraFacing)
        return this
    }


}