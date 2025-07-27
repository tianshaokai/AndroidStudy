package com.triversoft.common

import android.content.Context
import android.graphics.Typeface
import com.triversoft.common.text.TextFontConfig

class TypeFaceUtil private constructor() {

    var art: Typeface? = null
    var bold: Typeface? = null
    var light: Typeface? = null
    var medium: Typeface? = null
    var regular: Typeface? = null
    var semibold: Typeface? = null


    /**
     * 初始化字体，从 assets 文件夹中加载字体文件
     */
    fun initTypeFace(context: Context) {
        art = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_ART)
        light = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_LIGHT)
        regular = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_REGULAR)
        medium = Typeface.createFromAsset(context.assets, TextFontConfig.FONT_MEDIUM)
        semibold = Typeface.createFromAsset(context.assets, "fonts/Roboto-Bold.ttf")
        bold = Typeface.createFromAsset(context.assets, "fonts/Roboto-Bold.ttf")
    }

    companion object {
        @Volatile
        private var instance: TypeFaceUtil? = null

        /**
         * 获取单例实例
         */
        fun getInstance(): TypeFaceUtil {
            return instance ?: synchronized(this) {
                instance ?: TypeFaceUtil().also { instance = it }
            }
        }
    }
}