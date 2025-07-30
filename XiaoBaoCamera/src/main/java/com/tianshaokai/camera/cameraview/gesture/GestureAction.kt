package com.tianshaokai.camera.cameraview.gesture

enum class GestureAction(val value: Int, val type: GestureType) {
    NONE(0, GestureType.ONE_SHOT),
    AUTO_FOCUS(1, GestureType.ONE_SHOT),
    TAKE_PICTURE(2, GestureType.ONE_SHOT),
    ZOOM(3, GestureType.CONTINUOUS),
    EXPOSURE_CORRECTION(4, GestureType.CONTINUOUS),
    FILTER_CONTROL_1(5, GestureType.CONTINUOUS),
    FILTER_CONTROL_2(6, GestureType.CONTINUOUS);

    companion object {
        val DEFAULT_PINCH: GestureAction = NONE
        val DEFAULT_TAP: GestureAction = NONE
        val DEFAULT_LONG_TAP: GestureAction = NONE
        val DEFAULT_SCROLL_HORIZONTAL: GestureAction = NONE
        val DEFAULT_SCROLL_VERTICAL: GestureAction = NONE

        fun fromValue(value: Int): GestureAction? {
            return values().find { it.value == value }
        }
    }
}
