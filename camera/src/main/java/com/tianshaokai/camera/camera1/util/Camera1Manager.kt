package com.tianshaokai.camera.camera1.util

import android.hardware.Camera.CameraInfo
import com.tianshaokai.camera.type.CameraFacing

object Camera1Manager {

    /**
     * 获取相机ID
     */
    fun getCameraId(facing: CameraFacing): Int {
        return when (facing) {
            CameraFacing.FRONT -> CameraInfo.CAMERA_FACING_FRONT
            CameraFacing.BACK -> CameraInfo.CAMERA_FACING_BACK
        }
    }

    fun calcDisplayOrientation(screenOrientationDegrees: Int, cameraInfo: CameraInfo): Int {
        return if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
            (360 - (cameraInfo.orientation + screenOrientationDegrees) % 360) % 360
        } else {
            (cameraInfo.orientation - screenOrientationDegrees + 360) % 360
        }
    }


}