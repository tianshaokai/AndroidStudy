package com.tianshaokai.framework.util

import android.graphics.Color

/**
 * Author : tianshaokai
 * Date   : 2021/10/26 8:29 上午
 * Desc   :
 * Since  :
 */
class ColorUtils {

    companion object {
        /**
         * 修改颜色透明度
         */
        @JvmStatic fun changeAlpha(color: Int, fraction: Float): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            val alpha = (Color.alpha(color) * fraction).toInt()
            return Color.argb(alpha, red, green, blue)
        }
    }
}