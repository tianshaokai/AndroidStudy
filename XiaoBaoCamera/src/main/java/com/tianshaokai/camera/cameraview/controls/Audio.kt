package com.tianshaokai.camera.cameraview.controls

enum class Audio(val value: Int) : Control {
    OFF(0),
    ON(1),
    MONO(2),
    STEREO(3);

    companion object {
        val DEFAULT: Audio = ON

        fun fromValue(value: Int): Audio {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}