package com.tianshaokai.camera.cameraview.size

object SizeSelectors {

    interface Filter {
        fun accepts(size: Size): Boolean
    }

    fun withFilter(filter: Filter): SizeSelector {
        return object : SizeSelector {
            override fun select(sizes: List<Size>): List<Size> {
                return sizes.filter { filter.accepts(it) }
            }
        }
    }

    fun maxWidth(maxWidth: Int): SizeSelector {
        return withFilter(object : Filter {
            override fun accepts(size: Size): Boolean {
                return size.getWidth() <= maxWidth
            }
        })
    }

    fun minWidth(minWidth: Int): SizeSelector {
        return withFilter(object : Filter {
            override fun accepts(size: Size): Boolean {
                return size.getWidth() >= minWidth
            }
        })
    }

    fun maxHeight(maxHeight: Int): SizeSelector {
        return withFilter(object : Filter {
            override fun accepts(size: Size): Boolean {
                return size.getHeight() <= maxHeight
            }
        })
    }

    fun minHeight(minHeight: Int): SizeSelector {
        return withFilter(object : Filter {
            override fun accepts(size: Size): Boolean {
                return size.getHeight() >= minHeight
            }
        })
    }

    fun aspectRatio(aspectRatio: AspectRatio, tolerance: Float): SizeSelector {
        val targetRatio = aspectRatio.toFloat()
        return withFilter(object : Filter {
            override fun accepts(size: Size): Boolean {
                val currentRatio = AspectRatio.of(size.getWidth(), size.getHeight()).toFloat()
                return currentRatio >= targetRatio - tolerance && currentRatio <= targetRatio + tolerance
            }
        })
    }

    fun biggest(): SizeSelector {
        return object : SizeSelector {
            override fun select(sizes: List<Size>): List<Size> {
                return sizes.sortedDescending()
            }
        }
    }

    fun smallest(): SizeSelector {
        return object : SizeSelector {
            override fun select(sizes: List<Size>): List<Size> {
                return sizes.sorted()
            }
        }
    }

    fun maxArea(maxArea: Int): SizeSelector {
        return withFilter(object : Filter {
            override fun accepts(size: Size): Boolean {
                return size.getWidth() * size.getHeight() <= maxArea
            }
        })
    }

    fun minArea(minArea: Int): SizeSelector {
        return withFilter(object : Filter {
            override fun accepts(size: Size): Boolean {
                return size.getWidth() * size.getHeight() >= minArea
            }
        })
    }

    fun and(vararg selectors: SizeSelector): SizeSelector {
        return object : SizeSelector {
            override fun select(sizes: List<Size>): List<Size> {
                var result = sizes
                for (selector in selectors) {
                    result = selector.select(result)
                }
                return result
            }
        }
    }

    fun or(vararg selectors: SizeSelector): SizeSelector {
        return object : SizeSelector {
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
}
