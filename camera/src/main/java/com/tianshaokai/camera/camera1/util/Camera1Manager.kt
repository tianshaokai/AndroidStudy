package com.tianshaokai.camera.camera1.util

import android.hardware.Camera
import com.tianshaokai.camera.type.CameraFacing

object Camera1Manager {

    /**
     * 获取相机ID
     */
    fun getCameraId(facing: CameraFacing): Int {
        return when (facing) {
            CameraFacing.FRONT -> Camera.CameraInfo.CAMERA_FACING_FRONT
            CameraFacing.BACK -> Camera.CameraInfo.CAMERA_FACING_BACK
        }
    }


}