package com.tianshaokai.camera.cameraview.metering

import android.graphics.PointF
import android.graphics.RectF
import com.tianshaokai.camera.cameraview.size.Size

class MeteringRegion(val region: RectF, val weight: Int) : Comparable<MeteringRegion> {

    companion object {
        const val MAX_WEIGHT = 1000
    }

    // 区域变换
    fun transform(meteringTransform: MeteringTransform<MeteringRegion>): MeteringRegion {
        val transformedRect = RectF(
            Float.MAX_VALUE, Float.MAX_VALUE,
            Float.MIN_VALUE, Float.MIN_VALUE
        )
        val point = PointF()

        // 左上角
        point.set(region.left, region.top)
        updateRect(transformedRect, meteringTransform.transformMeteringPoint(point))

        // 右上角
        point.set(region.right, region.top)
        updateRect(transformedRect, meteringTransform.transformMeteringPoint(point))

        // 右下角
        point.set(region.right, region.bottom)
        updateRect(transformedRect, meteringTransform.transformMeteringPoint(point))

        // 左下角
        point.set(region.left, region.bottom)
        updateRect(transformedRect, meteringTransform.transformMeteringPoint(point))

        return MeteringRegion(transformedRect, weight)
    }

    // 更新矩形边界
    private fun updateRect(rect: RectF, point: PointF) {
        rect.left = minOf(rect.left, point.x)
        rect.top = minOf(rect.top, point.y)
        rect.right = maxOf(rect.right, point.x)
        rect.bottom = maxOf(rect.bottom, point.y)
    }

    // 区域裁剪（通过 Size）
    fun clip(size: Size): MeteringRegion {
        return clip(RectF(0f, 0f, size.getWidth().toFloat(), size.getHeight().toFloat()))
    }

    // 区域裁剪（通过 RectF）
    fun clip(rect: RectF): MeteringRegion {
        val clippedRect = RectF(
            maxOf(rect.left, region.left),
            maxOf(rect.top, region.top),
            minOf(rect.right, region.right),
            minOf(rect.bottom, region.bottom)
        )
        return MeteringRegion(clippedRect, weight)
    }

    // 比较方法（根据权重降序排序）
    override fun compareTo(other: MeteringRegion): Int {
        return -weight.compareTo(other.weight)
    }
}