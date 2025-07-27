package com.tianshaokai.camera.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class ViewGesture @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var allowSwipe: Boolean = true
    private var iListenGesture: IListenGesture? = null
    private var spaceDoneEvent: Int = 0
    private var xDown: Float = 0f
    private var xMove: Float = 0f

    init {
        // 初始化触发滑动事件的最小距离
        spaceDoneEvent = context.resources.displayMetrics.widthPixels / 4
    }

    interface IListenGesture {
        fun toLeft()
        fun toRight()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.actionIndex != 0) {
            allowSwipe = false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xDown = event.x
            }
            MotionEvent.ACTION_UP -> {
                allowSwipe = true
            }
            MotionEvent.ACTION_MOVE -> {
                xMove = event.x
                handleEvent()
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    private fun handleEvent() {
        if (allowSwipe) {
            val deltaX = xMove - xDown
            if (deltaX > spaceDoneEvent) {
                iListenGesture?.let {
                    allowSwipe = false
                    it.toLeft()
                }
            } else if (deltaX < -spaceDoneEvent) {
                iListenGesture?.let {
                    allowSwipe = false
                    it.toRight()
                }
            }
        }
    }

    fun setListenGesture(listener: IListenGesture) {
        iListenGesture = listener
    }
}