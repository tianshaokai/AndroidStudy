package com.tianshaokai.camera.cameraview.controls

enum class Flash(val value: Int) : Control {
    OFF(0),
    ON(1),
    AUTO(2),
    TORCH(3);

    companion object {
        val DEFAULT: Flash = OFF

        fun fromValue(value: Int): Flash {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}