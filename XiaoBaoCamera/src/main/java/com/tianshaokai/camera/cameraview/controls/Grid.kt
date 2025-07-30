package com.tianshaokai.camera.cameraview.controls

enum class Grid(val value: Int) : Control {
    OFF(0),
    DRAW_3X3(1),
    DRAW_4X4(2),
    DRAW_PHI(3);

    companion object {
        val DEFAULT: Grid = OFF

        fun fromValue(value: Int): Grid {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}