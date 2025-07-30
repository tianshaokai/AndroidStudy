package com.tianshaokai.camera.cameraview.controls

enum class Engine(val value: Int) : Control {
    CAMERA1(0),
    CAMERA2(1);

    companion object {
        val DEFAULT: Engine = CAMERA1

        fun fromValue(value: Int): Engine {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}