package com.tianshaokai.camera.type

class Size(val width: Int, val height: Int) : Comparable<Size> {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this === other) {
            return true
        }
        if (other is Size) {
            return width == other.width && height == other.height
        }
        return false
    }

    override fun compareTo(other: Size): Int {
        val areaDiff = width * height - other.width * other.height
        return if (areaDiff > 0) {
            1
        } else if (areaDiff < 0) {
            -1
        } else {
            0
        }
    }

    override fun hashCode(): Int {
        return height xor ((width shl (Integer.SIZE / 2)) or (width ushr (Integer.SIZE / 2)));
    }
}