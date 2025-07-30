package com.tianshaokai.camera.cameraview.controls

enum class PictureFormat(val value: Int) : Control {
    JPEG(0),
    DNG(1);

    companion object {
        val DEFAULT: PictureFormat = JPEG

        fun fromValue(value: Int): PictureFormat {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}