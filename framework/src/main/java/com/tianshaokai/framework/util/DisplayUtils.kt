package com.tianshaokai.framework.util

import android.content.Context
import android.util.DisplayMetrics

object DisplayUtils {

    /**
     * @param context 上下文
     * @return DisplayMetrics对象
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        return context.resources.displayMetrics
    }

    fun getScreenOrientation(context: Context):Int {
        return context.resources.configuration.orientation
    }

    /**
     * 获取屏幕分辨率-宽
     *
     * @param context 上下文
     * @return 宽
     */
    fun getScreenWidth(context: Context?): Int {
        if (context == null) return 0
        val metrics: DisplayMetrics = getDisplayMetrics(context)
        return metrics.widthPixels
    }

    /**
     * 获取屏幕分辨率-高
     *
     * @param context 上下文
     * @return 高
     */
    fun getScreenHeight(context: Context?): Int {
        if (context == null) return 0
        val metrics: DisplayMetrics = getDisplayMetrics(context)
        return metrics.heightPixels
    }

}