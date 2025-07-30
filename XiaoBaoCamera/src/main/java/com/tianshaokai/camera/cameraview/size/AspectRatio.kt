package com.tianshaokai.camera.cameraview.size

class AspectRatio private constructor(private val x: Int, private val y: Int) : Comparable<AspectRatio> {

    companion object {
        private val cache = mutableMapOf<String, AspectRatio>()

        fun of(size: Size): AspectRatio {
            return of(size.getWidth(), size.getHeight())
        }

        fun of(width: Int, height: Int): AspectRatio {
            val gcdValue = gcd(width, height)
            val normalizedX = width / gcdValue
            val normalizedY = height / gcdValue
            val key = "$normalizedX:$normalizedY"
            return cache[key] ?: AspectRatio(normalizedX, normalizedY).also {
                cache[key] = it
            }
        }

        fun parse(value: String): AspectRatio {
            val parts = value.split(":")
            if (parts.size != 2) {
                throw NumberFormatException("Illegal AspectRatio string. Must be x:y")
            }
            return of(parts[0].toInt(), parts[1].toInt())
        }

        private fun gcd(a: Int, b: Int): Int {
            var x = a
            var y = b
            while (y != 0) {
                val temp = x % y
                x = y
                y = temp
            }
            return x
        }
    }

    fun getX(): Int = x

    fun getY(): Int = y

    fun matches(size: Size): Boolean {
        val gcdValue = gcd(size.getWidth(), size.getHeight())
        return x == size.getWidth() / gcdValue && y == size.getHeight() / gcdValue
    }

    fun matches(size: Size, tolerance: Float): Boolean {
        val currentRatio = size.getWidth().toFloat() / size.getHeight().toFloat()
        return kotlin.math.abs(toFloat() - currentRatio) <= tolerance
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is AspectRatio) return false
        return x == other.x && y == other.y
    }

    override fun toString(): String {
        return "$x:$y"
    }

    fun toFloat(): Float {
        return x.toFloat() / y.toFloat()
    }

    override fun hashCode(): Int {
        return y xor ((x ushr 16) or (x shl 16))
    }

    override fun compareTo(other: AspectRatio): Int {
        if (equals(other)) return 0
        return if (toFloat() - other.toFloat() > 0.0f) 1 else -1
    }

    fun flip(): AspectRatio {
        return of(y, x)
    }
}
