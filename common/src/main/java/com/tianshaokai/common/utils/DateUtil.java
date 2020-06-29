package com.tianshaokai.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtil {

    public static final String Format_H_m_s = "HH:mm:ss";
    public static final String Format_H_m = "HH:mm";
    public static final String Format_m_s = "mm:ss";

    public static final String Format_y_M_d_H_m_s = "yyyy-MM-dd HH:mm:ss";
    public static final String Format_y_M_d_H_m_s_2 = "yyyy-MM-dd-HH-mm-ss";
    public static final String Format_y_M_d_H_m = "yyyy-MM-dd HH:mm";
    public static final String Format_y_M_d = "yyyy-MM-dd";
    public static final String Format_yMd_Hms = "yyyyMMdd_HHmmss";


    private static ThreadLocal<Map<String, SimpleDateFormat>> sThreadLocal = new ThreadLocal<>();

    private static SimpleDateFormat getDateFormat(String pattern) {
        Map<String, SimpleDateFormat> strDateFormatMap = sThreadLocal.get();
        if (strDateFormatMap == null) {
            strDateFormatMap = new HashMap<>();
        }
        SimpleDateFormat simpleDateFormat = strDateFormatMap.get(pattern);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(pattern, Locale.CANADA);
            strDateFormatMap.put(pattern, simpleDateFormat);
            sThreadLocal.set(strDateFormatMap);
        }
        return simpleDateFormat;
    }

    public static String getStringDate(String formatType) {
        SimpleDateFormat simpleDateFormat = getDateFormat(formatType);
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    public static String getTimeStamp() {
        return getStringDate(Format_yMd_Hms);
    }
}
