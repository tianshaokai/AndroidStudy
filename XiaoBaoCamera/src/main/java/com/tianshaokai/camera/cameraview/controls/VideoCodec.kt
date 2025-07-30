package com.tianshaokai.camera.cameraview.controls

enum class VideoCodec(val value: Int) : Control {
    DEVICE_DEFAULT(0),
    H_263(1),
    H_264(2);

    companion object {
        val DEFAULT: VideoCodec = DEVICE_DEFAULT

        fun fromValue(value: Int): VideoCodec {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}