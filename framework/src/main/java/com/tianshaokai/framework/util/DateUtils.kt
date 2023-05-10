package com.tianshaokai.framework.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    const val DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT_DATE = "yyyy-MM-dd"
    const val DATE_FORMAT_TIME = "HH:mm:ss"

    const val Format_H_m = "HH:mm"
    const val Format_m_s = "mm:ss"

    const val Format_y_M_d_H_m_s_2 = "yyyy-MM-dd-HH-mm-ss"
    const val Format_y_M_d_H_m = "yyyy-MM-dd HH:mm"
    const val Format_yMd_Hms = "yyyyMMdd_HHmmss"


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
        val simpleDateFormat = getDateFormat(DATE_FORMAT_DATETIME)
        return simpleDateFormat.format(Date(date))
    }

    fun formatDate(date: Long): String {
        val simpleDateFormat = getDateFormat(DATE_FORMAT_DATE)
        return simpleDateFormat.format(Date(date))
    }

    fun formatTime(date: Long): String {
        val simpleDateFormat = getDateFormat(DATE_FORMAT_TIME)
        return simpleDateFormat.format(Date(date))
    }

}