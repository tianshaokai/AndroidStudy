package com.tianshaokai.camera.cameraview.controls

import android.content.Context
import com.tianshaokai.camera.cameraview.CameraUtils

enum class Facing(val value: Int) : Control {
    BACK(0),
    FRONT(1);

    companion object {
        fun DEFAULT(context: Context?): Facing {
            if (context == null) {
                return BACK
            }
            val facing = BACK
            return if (CameraUtils.hasCameraFacing(context, facing)) {
                facing
            } else {
                val facing2 = FRONT
                if (CameraUtils.hasCameraFacing(context, facing2)) facing2 else facing
            }
        }

        fun fromValue(value: Int): Facing? {
            return values().find { it.value == value }
        }
    }
}
