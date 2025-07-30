package com.tianshaokai.camera.cameraview.metering

import android.graphics.PointF
import android.graphics.RectF
import com.tianshaokai.camera.cameraview.size.Size

class MeteringRegions private constructor(private val regions: List<MeteringRegion>) {

    companion object {
        private const val BLUR_FACTOR_SIZE = 1.5f
        private const val BLUR_FACTOR_WEIGHT = 0.1f
        private const val POINT_AREA = 0.05f

        // 从点创建 MeteringRegions
        fun fromPoint(size: Size, point: PointF): MeteringRegions {
            return fromPoint(size, point, MeteringRegion.MAX_WEIGHT)
        }

        fun fromPoint(size: Size, point: PointF, weight: Int): MeteringRegions {
            val expandedArea = expand(point, size.getWidth() * POINT_AREA, size.getHeight() * POINT_AREA)
            return fromArea(size, expandedArea, weight, true)
        }

        // 从区域创建 MeteringRegions
        fun fromArea(size: Size, rect: RectF): MeteringRegions {
            return fromArea(size, rect, MeteringRegion.MAX_WEIGHT)
        }

        fun fromArea(size: Size, rect: RectF, weight: Int): MeteringRegions {
            return fromArea(size, rect, weight, false)
        }

        fun fromArea(size: Size, rect: RectF, weight: Int, blur: Boolean): MeteringRegions {
            val regions = mutableListOf<MeteringRegion>()
            val center = PointF(rect.centerX(), rect.centerY())
            val width = rect.width()
            val height = rect.height()

            // 添加主区域
            regions.add(MeteringRegion(rect, weight))

            // 添加模糊区域
            if (blur) {
                val blurredRegion = expand(center, width * BLUR_FACTOR_SIZE, height * BLUR_FACTOR_SIZE)
                regions.add(MeteringRegion(blurredRegion, (weight * BLUR_FACTOR_WEIGHT).toInt()))
            }

            // 裁剪区域
            val clippedRegions = regions.map { it.clip(size) }
            return MeteringRegions(clippedRegions)
        }

        // 扩展点到矩形区域
        private fun expand(point: PointF, width: Float, height: Float): RectF {
            val halfWidth = width / 2.0f
            val halfHeight = height / 2.0f
            return RectF(
                point.x - halfWidth,
                point.y - halfHeight,
                point.x + halfWidth,
                point.y + halfHeight
            )
        }
    }

    // 区域变换
    fun transform(meteringTransform: MeteringTransform<MeteringRegion>): MeteringRegions {
        val transformedRegions = regions.map { it.transform(meteringTransform) }
        return MeteringRegions(transformedRegions)
    }

    // 获取变换后的区域列表
    fun <T> get(limit: Int, meteringTransform: MeteringTransform<T>): List<T> {
        val sortedRegions = regions.sorted() // 根据权重排序
        val transformedRegions = sortedRegions.map {
            meteringTransform.transformMeteringRegion(it.region, it.weight)
        }
        return transformedRegions.take(limit) // 返回限制数量的区域
    }
}