package com.triversoft.common

import android.graphics.Color

class ColorUtil private constructor() {

    var colorText: Int = 0

    var colorEditText: Int = 0

    var colorSelected: Int = 0

    var colorTextSecondary: Int = 0

    var colorBackground: Int = 0

    var colorIcon: Int = 0


    /**
     * 初始化颜色值
     */
    fun initColor(
        textColor: String = "#000000",
        colorSelected: String = "#febd00",
        colorEditText: String = "#252525",
        colorTextSecondary: String = "#252525",
        backgroundColor: String = "#f7f7f7",
        iconColor: String = "#febd00"
    ) {
        colorText = Color.parseColor(textColor)
        this.colorSelected = Color.parseColor(colorSelected)
        this.colorEditText = Color.parseColor(colorEditText)
        this.colorTextSecondary = Color.parseColor(colorTextSecondary)
        this.colorBackground = Color.parseColor(backgroundColor)
        this.colorIcon = Color.parseColor(iconColor)
    }

    companion object {
        @Volatile
        private var instance: ColorUtil? = null

        /**
         * 获取单例实例
         */
        fun getInstance(): ColorUtil {
            return instance ?: synchronized(this) {
                instance ?: ColorUtil().also { instance = it }
            }
        }
    }
}
