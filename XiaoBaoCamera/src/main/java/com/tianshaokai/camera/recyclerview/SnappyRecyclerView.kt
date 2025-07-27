package com.tianshaokai.camera.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import kotlin.jvm.internal.Intrinsics
import kotlin.math.abs
import kotlin.math.roundToInt

class SnappyRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {

    private var snapHelper: SnapHelper

    init {
        val myLinearSnapHelper = MyLinearSnapHelper()
        this.snapHelper = myLinearSnapHelper
        myLinearSnapHelper.attachToRecyclerView(this)
        addFadeOut()
    }

    private fun addFadeOut() {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Intrinsics.checkNotNullParameter(recyclerView, "recyclerView")
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                Intrinsics.checkNotNullParameter(recyclerView, "recyclerView")
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    fun getPosition(): Int {
        try {
            val snapHelper = this.snapHelper
            Intrinsics.checkNotNull(snapHelper)
            val viewFindSnapView = snapHelper.findSnapView(layoutManager)
            Intrinsics.checkNotNull(viewFindSnapView)
            return getChildAdapterPosition(viewFindSnapView!!)
        } catch (e: Exception) {
            return 4
        }
    }

    fun getViewWidth(): Int {
        return resources.displayMetrics.widthPixels
    }

    private fun setScale(percentFromCenter: Float, currentView: View) {
        val f = ((1.0f - percentFromCenter) * 0.5f) + 1.0f
        currentView.scaleX = f
        currentView.scaleY = f
    }

    private fun setAlpha(percentFromCenter: Float, currentView: View) {
        currentView.alpha = ((1.0f - percentFromCenter) * 0.8f) + 0.2f
    }
}


class MyLinearSnapHelper : SnapHelper() {

    private var mHorizontalHelper: OrientationHelper? = null
    private var mVerticalHelper: OrientationHelper? = null

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View): IntArray {
        Intrinsics.checkNotNullParameter(layoutManager, "layoutManager");
        Intrinsics.checkNotNullParameter(targetView, "targetView");
        val iArray = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            iArray[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager))
        } else {
            iArray[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            iArray[1] = distanceToCenter(layoutManager, targetView, getVerticalHelper(layoutManager))
        } else {
            iArray[1] = 0
        }
        return iArray
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager?): View? {
        // 如果支持垂直滚动，返回垂直方向上最接近中心的视图
        if (layoutManager?.canScrollVertically() == true) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager))
        }
        // 如果支持水平滚动，返回水平方向上最接近中心的视图
        if (layoutManager?.canScrollHorizontally() == true) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager))
        }
        // 如果既不支持垂直滚动也不支持水平滚动，返回 null
        return null
    }

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int): Int {
        // 检查参数是否有效
        require(layoutManager is RecyclerView.SmoothScroller.ScrollVectorProvider) { "LayoutManager must implement ScrollVectorProvider" }

        val itemCount = layoutManager.itemCount
        if (itemCount == 0) return -1

        // 找到当前对齐的视图
        val currentSnapView = findSnapView(layoutManager) ?: return -1
        val currentPosition = layoutManager.getPosition(currentSnapView)
        if (currentPosition == -1) return -1

        // 获取滚动向量
        val scrollVector = (layoutManager as RecyclerView.SmoothScroller.ScrollVectorProvider)
            .computeScrollVectorForPosition(itemCount - 1) ?: return -1

        // 计算水平滚动的目标偏移量
        var horizontalOffset = 0
        if (layoutManager.canScrollHorizontally()) {
            horizontalOffset = estimateNextPositionDiffForFling(layoutManager, getHorizontalHelper(layoutManager), velocityX, 0)
            if (scrollVector.x < 0) {
                horizontalOffset = -horizontalOffset
            }
        }

        // 计算垂直滚动的目标偏移量
        var verticalOffset = 0
        if (layoutManager.canScrollVertically()) {
            verticalOffset = estimateNextPositionDiffForFling(layoutManager, getVerticalHelper(layoutManager), 0, velocityY)
            if (scrollVector.y < 0) {
                verticalOffset = -verticalOffset
            }
        }

        // 如果支持垂直滚动，优先使用垂直偏移量
        val targetOffset = if (layoutManager.canScrollVertically()) verticalOffset else horizontalOffset
        if (targetOffset == 0) return -1

        // 计算目标位置
        val targetPosition = currentPosition + targetOffset
        return targetPosition.coerceIn(0, itemCount - 1) // 确保目标位置在合法范围内
    }


    private fun distanceToCenter(layoutManager: RecyclerView.LayoutManager, targetView: View, helper: OrientationHelper): Int {
        val decoratedStart = helper.getDecoratedStart(targetView) + (helper.getDecoratedMeasurement(targetView) / 2)
        val end = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + (helper.totalSpace / 2)
        } else {
            helper.end / 2
        }
        return decoratedStart - end
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        // 如果 mHorizontalHelper 已经存在并且它的 LayoutManager 与传入的 layoutManager 相同，则直接返回
        if (mHorizontalHelper != null && mHorizontalHelper?.layoutManager == layoutManager) {
            return mHorizontalHelper!!
        }
        // 否则，创建一个新的 HorizontalHelper 并赋值给 mHorizontalHelper
        mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        return mHorizontalHelper!!
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        // 如果 mVerticalHelper 已经存在并且它的 LayoutManager 与传入的 layoutManager 相同，则直接返回
        if (mVerticalHelper != null && mVerticalHelper?.layoutManager == layoutManager) {
            return mVerticalHelper!!
        }
        // 否则，创建一个新的 VerticalHelper 并赋值给 mVerticalHelper
        mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        return mVerticalHelper!!
    }

    private fun estimateNextPositionDiffForFling(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper,
        velocityX: Int,
        velocityY: Int
    ): Int {
        // 计算滚动距离
        val scrollDistance = calculateScrollDistance(velocityX, velocityY)
        // 计算每个子项的平均距离
        val distancePerChild = computeDistancePerChild(layoutManager, helper)
        if (distancePerChild <= 0.0f) {
            return 0 // 如果平均距离无效，返回 0
        }
        // 选择滚动方向并计算目标偏移量
        val dominantScrollDistance = if (abs(scrollDistance[0]) > abs(scrollDistance[1])) {
            scrollDistance[0] // 水平滚动距离
        } else {
            scrollDistance[1] // 垂直滚动距离
        }
        return (dominantScrollDistance / distancePerChild).roundToInt()
    }

    private fun findCenterView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        // 特殊情况处理：LinearLayoutManager
        if (layoutManager is LinearLayoutManager) {
            val firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (firstCompletelyVisiblePosition == 0) {
                return layoutManager.getChildAt(0)
            }
            val lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()
            if (lastCompletelyVisiblePosition == layoutManager.itemCount - 1) {
                return layoutManager.getChildAt(layoutManager.itemCount - 1)
            }
        }

        // 获取子项数量
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null // 如果没有子项，返回 null
        }

        // 计算 RecyclerView 的中心点
        val recyclerViewCenter = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + (helper.totalSpace / 2)
        } else {
            helper.end / 2
        }

        var closestChild: View? = null // 距离中心最近的子项
        var smallestDistance = Int.MAX_VALUE // 最小距离

        // 遍历所有子项
        for (index in 0 until childCount) {
            val child = layoutManager.getChildAt(index) ?: continue
            val childCenter = helper.getDecoratedStart(child) + (helper.getDecoratedMeasurement(child) / 2)
            val distanceToCenter = abs(childCenter - recyclerViewCenter)

            // 更新距离最近的子项
            if (distanceToCenter < smallestDistance) {
                closestChild = child
                smallestDistance = distanceToCenter
            }
        }

        return closestChild
    }

    private fun computeDistancePerChild(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): Float {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return 1.0f // 如果没有子项，返回默认值
        }

        var minPositionView: View? = null // 位置最小的子项
        var maxPositionView: View? = null // 位置最大的子项
        var minPosition = Int.MAX_VALUE // 最小位置
        var maxPosition = Int.MIN_VALUE // 最大位置

        // 遍历所有子项
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i) ?: continue
            val position = layoutManager.getPosition(child)
            if (position != -1) {
                if (position < minPosition) {
                    minPosition = position
                    minPositionView = child
                }
                if (position > maxPosition) {
                    maxPosition = position
                    maxPositionView = child
                }
            }
        }

        // 如果无法找到有效的子项，返回默认值
        if (minPositionView == null || maxPositionView == null) {
            return 1.0f
        }

        // 计算两个子项之间的总距离
        val totalDistance = maxOf(
            helper.getDecoratedEnd(maxPositionView),
            helper.getDecoratedEnd(minPositionView)
        ) - minOf(
            helper.getDecoratedStart(maxPositionView),
            helper.getDecoratedStart(minPositionView)
        )

        // 如果总距离为 0，返回默认值
        if (totalDistance == 0) {
            return 1.0f
        }

        // 计算每个子项的平均距离
        return totalDistance.toFloat() / ((maxPosition - minPosition) + 1)
    }
}