package com.tianshaokai.studyuiapp

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Author : tianshaokai
 * Date   : 2019/10/15 10:25
 * Desc   :
 * Since  :
 */
class StringTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_string_test)

        val str = String.format("%s/%s", 121212, 987654321)
        val spanStr = SpannableString(str)
        //设置文字的大小
        spanStr.setSpan(
            AbsoluteSizeSpan(100),
            0,
            str.indexOf("/"),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spanStr.setSpan(
            AbsoluteSizeSpan(50),
            str.indexOf("/"),
            str.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text.setText(spanStr)

    }

}