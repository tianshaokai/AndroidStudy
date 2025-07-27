package com.triversoft.common.text

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.tianshaokai.camera.R
import com.triversoft.common.ColorUtil
import com.triversoft.common.TypeFaceUtil

class TextViewMedium @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    init {
        // 设置默认字体
        val typefaceUtil = TypeFaceUtil.getInstance()
        requireNotNull(typefaceUtil) { "TypeFaceUtil instance is null" }
        typeface = typefaceUtil.medium

        // 如果有 AttributeSet，则初始化属性
        if (attrs != null) {
            initAttr(attrs)
        } else {
            // 如果没有 AttributeSet，则设置默认文本颜色
            val colorUtil = ColorUtil.getInstance()
            requireNotNull(colorUtil) { "ColorUtil instance is null" }
            setTextColor(colorUtil.colorText)
        }
    }

    private fun initAttr(attrs: AttributeSet) {
        // 从 XML 属性中解析自定义样式
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.text_style, 0, 0)
        requireNotNull(typedArray) { "Failed to obtain styled attributes" }

        // 如果属性 textColorDefault 为 true，则设置默认文本颜色
        if (typedArray.getBoolean(R.styleable.text_style_textColorDefault, true)) {
            val colorUtil = ColorUtil.getInstance()
            requireNotNull(colorUtil) { "ColorUtil instance is null" }
            setTextColor(colorUtil.colorText)
        }

        // 回收 TypedArray
        typedArray.recycle()
    }
}
