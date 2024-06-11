package com.tianshaokai.camera.type

import androidx.collection.ArrayMap
import java.util.SortedSet
import java.util.TreeSet

class SizeMap {

    private val mRatios: ArrayMap<AspectRatio, SortedSet<Size>> = ArrayMap()

    fun add(size: Size): Boolean {
        for (ratio in mRatios.keys) {
            if (ratio.matches(size)) {
                val sizes = mRatios[ratio]!!
                if (sizes.contains(size)) {
                    return false
                } else {
                    sizes.add(size)
                    return true
                }
            }
        }
        // None of the existing ratio matches the provided size; add a new key
        val sizes: SortedSet<Size> = TreeSet()
        sizes.add(size)
        mRatios[AspectRatio.of(size.width, size.height)] = sizes
        return true
    }


    fun remove(ratio: AspectRatio?) {
        mRatios.remove(ratio)
    }

    fun ratios(): Set<AspectRatio> {
        return mRatios.keys
    }

    fun sizes(ratio: AspectRatio): SortedSet<Size>? {
        return mRatios[ratio]
    }

    fun clear() {
        mRatios.clear()
    }

    fun isEmpty(): Boolean {
        return mRatios.isEmpty
    }


}