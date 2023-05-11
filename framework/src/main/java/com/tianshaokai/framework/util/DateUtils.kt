package com.tianshaokai.framework.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    //format格式,有缺少的参照样式增加即可
    const val FORMAT_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_yyyy_MM_dd_HH_mm_ss_2 = "yyyy_MM_dd_HH_mm_ss"
    const val FORMAT_yyyy_MM_dd_HH_mm = "yyyy年MM月dd日 HH:mm"

    const val FORMAT_yyyy_MM_dd = "yyyy-MM-dd"
    const val FORMAT_yyyyMMdd = "yyyyMMdd"

    const val FORMAT_HH_mm_ss = "HH:mm:ss"

    const val FORMAT_MM_dd_HH_mm = "MM月dd日 HH:mm"

    const val FORMAT_MM_dd = "MM月dd日"
    const val FORMAT_M_d = "M月d日"

    const val FORMAT_HH_mm = "HH:mm"
    const val FORMAT_mm_ss = "mm:ss"

    //时区，有缺少的参照样式增加即可
    const val TIME_CHINA = "Asia/Shanghai"

    const val TIME_H_24 = (1000 * 60 * 60 * 24).toLong()


    private val sThreadLocal = ThreadLocal<MutableMap<String, SimpleDateFormat>>()

    private fun getDateFormat(pattern: String): SimpleDateFormat {
        var strDateFormatMap = sThreadLocal.get()
        if (strDateFormatMap == null) {
            strDateFormatMap = HashMap()
        }
        var simpleDateFormat = strDateFormatMap[pattern]
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat(pattern, Locale.CANADA)
            strDateFormatMap.put(pattern, simpleDateFormat)
            sThreadLocal.set(strDateFormatMap)
        }
        return simpleDateFormat
    }

    fun getStringDate(formatType: String): String {
        val simpleDateFormat = getDateFormat(formatType)
        val date = Date()
        return simpleDateFormat.format(date)
    }


    fun formatDataTime(date: Long): String {
        val simpleDateFormat = getDateFormat(FORMAT_yyyy_MM_dd_HH_mm_ss)
        return simpleDateFormat.format(Date(date))
    }

    fun formatDate(date: Long): String {
        val simpleDateFormat = getDateFormat(FORMAT_yyyy_MM_dd)
        return simpleDateFormat.format(Date(date))
    }

    fun formatTime(date: Long): String {
        val simpleDateFormat = getDateFormat(FORMAT_HH_mm_ss)
        return simpleDateFormat.format(Date(date))
    }

}