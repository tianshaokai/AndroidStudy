package com.tianshaokai.camera.cameraview.size

class Size(private val width: Int, private val height: Int) : Comparable<Size> {

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }

    fun flip(): Size {
        return Size(height, width)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is Size) return false
        return width == other.width && height == other.height
    }

    override fun toString(): String {
        return "$width x $height"
    }

    override fun hashCode(): Int {
        return height xor ((width ushr 16) or (width shl 16))
    }

    override fun compareTo(other: Size): Int {
        return (width * height) - (other.width * other.height)
    }
}