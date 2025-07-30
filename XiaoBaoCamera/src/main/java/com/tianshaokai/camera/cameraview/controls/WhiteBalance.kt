package com.tianshaokai.camera.cameraview.controls

enum class WhiteBalance(val value: Int) : Control {
    AUTO(0),
    INCANDESCENT(1),
    FLUORESCENT(2),
    DAYLIGHT(3),
    CLOUDY(4);

    companion object {
        val DEFAULT: WhiteBalance = AUTO

        fun fromValue(value: Int): WhiteBalance {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}