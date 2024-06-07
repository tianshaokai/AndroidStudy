package com.tianshaokai.camera.api

import android.view.SurfaceHolder
import com.tianshaokai.camera.camera1.basic.Camera1Engine
import com.tianshaokai.camera.type.CameraFacing

interface CameraActions {
    fun startPreview(surfaceHolder: SurfaceHolder)

    fun stopPreview()

    /**
     * 设置摄像头前后置
     */
    fun setCameraFacing(cameraFacing: CameraFacing): Camera1Engine
}