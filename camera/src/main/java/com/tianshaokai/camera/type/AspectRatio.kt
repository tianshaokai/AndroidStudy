package com.tianshaokai.camera.type

import androidx.collection.SparseArrayCompat

class AspectRatio private constructor(var mX: Int, var mY:Int): Comparable<AspectRatio> {

    fun matches(size: Size): Boolean {
        val gcd: Int = gcd(size.width, size.height)
        val x: Int = size.width / gcd
        val y: Int = size.height / gcd
        return mX == x && mY == y
    }


    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this === other) {
            return true
        }
        if (other is AspectRatio) {
            return mX == other.mX && mY == other.mY
        }
        return false
    }

    private fun toFloat(): Float {
        return mX.toFloat() / mY
    }

    override fun compareTo(other: AspectRatio): Int {
        if (equals(other)) {
            return 0
        } else if (toFloat() - other.toFloat() > 0) {
            return 1
        }
        return -1
    }

    override fun hashCode(): Int {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return mY xor ((mX shl (Integer.SIZE / 2)) or (mX ushr (Integer.SIZE / 2)))
    }

    companion object {
        private val sCache: SparseArrayCompat<SparseArrayCompat<AspectRatio>> = SparseArrayCompat(16)
        private fun gcd(x: Int, y: Int): Int {
            var a = x
            var b = y
            while (b != 0) {
                val c = b
                b = a % b
                a = c
            }
            return a
        }

        fun of(a: Int, b: Int): AspectRatio {
            var x = a
            var y = b
            val gcd: Int = gcd(x, y)
            x /= gcd
            y /= gcd
            var arrayX: SparseArrayCompat<AspectRatio>? = sCache.get(x)
            if (arrayX == null) {
                val ratio = AspectRatio(x, y)
                arrayX = SparseArrayCompat()
                arrayX.put(y, ratio)
                sCache.put(x, arrayX)
                return ratio
            } else {
                var ratio: AspectRatio? = arrayX.get(y)
                if (ratio == null) {
                    ratio = AspectRatio(x, y)
                    arrayX.put(y, ratio)
                }
                return ratio
            }
        }
    }
}