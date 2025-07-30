package com.tianshaokai.camera.cameraview.controls

enum class Mode(val value: Int) : Control {
    PICTURE(0),
    VIDEO(1);

    companion object {
        val DEFAULT: Mode = PICTURE

        fun fromValue(value: Int): Mode {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}
