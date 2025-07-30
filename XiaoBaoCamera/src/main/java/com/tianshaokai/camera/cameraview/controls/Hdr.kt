package com.tianshaokai.camera.cameraview.controls

enum class Hdr(val value: Int) : Control {
    OFF(0),
    ON(1);

    companion object {
        val DEFAULT: Hdr = OFF

        fun fromValue(value: Int): Hdr {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}