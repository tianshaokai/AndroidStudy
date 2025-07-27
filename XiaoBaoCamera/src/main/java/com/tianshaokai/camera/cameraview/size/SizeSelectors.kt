package com.tianshaokai.camera.cameraview.size

object SizeSelectors {

    interface Filter {
        fun accepts(size: Size): Boolean
    }

    fun withFilter(filter: Filter): SizeSelector {
        return FilterSelector(filter)
    }

    fun maxWidth(maxWidth: Int): SizeSelector {
        return withFilter { size -> size.getWidth() <= maxWidth }
    }

    fun minWidth(minWidth: Int): SizeSelector {
        return withFilter { size -> size.getWidth() >= minWidth }
    }

    fun maxHeight(maxHeight: Int): SizeSelector {
        return withFilter { size -> size.getHeight() <= maxHeight }
    }

    fun minHeight(minHeight: Int): SizeSelector {
        return withFilter { size -> size.getHeight() >= minHeight }
    }

    fun aspectRatio(aspectRatio: AspectRatio, tolerance: Float): SizeSelector {
        val targetRatio = aspectRatio.toFloat()
        return withFilter { size ->
            val currentRatio = AspectRatio.of(size.getWidth(), size.getHeight()).toFloat()
            currentRatio >= targetRatio - tolerance && currentRatio <= targetRatio + tolerance
        }
    }

    fun biggest(): SizeSelector {
        return SizeSelector { sizes ->
            sizes.sortedDescending()
        }
    }

    fun smallest(): SizeSelector {
        return SizeSelector { sizes ->
            sizes.sorted()
        }
    }

    fun maxArea(maxArea: Int): SizeSelector {
        return withFilter { size -> size.getWidth() * size.getHeight() <= maxArea }
    }

    fun minArea(minArea: Int): SizeSelector {
        return withFilter { size -> size.getWidth() * size.getHeight() >= minArea }
    }

    fun and(vararg selectors: SizeSelector): SizeSelector {
        return AndSelector(*selectors)
    }

    fun or(vararg selectors: SizeSelector): SizeSelector {
        return OrSelector(*selectors)
    }

    private class FilterSelector(private val filter: Filter) : SizeSelector {
        override fun select(sizes: List<Size>): List<Size> {
            return sizes.filter { filter.accepts(it) }
        }
    }

    private class AndSelector(private vararg val selectors: SizeSelector) : SizeSelector {
        override fun select(sizes: List<Size>): List<Size> {
            var result = sizes
            for (selector in selectors) {
                result = selector.select(result)
            }
            return result
        }
    }

    private class OrSelector(private vararg val selectors: SizeSelector) : SizeSelector {
        override fun select(sizes: List<Size>): List<Size> {
            for (selector in selectors) {
                val result = selector.select(sizes)
                if (result.isNotEmpty()) {
                    return result
                }
            }
            return emptyList()
        }
    }
}
