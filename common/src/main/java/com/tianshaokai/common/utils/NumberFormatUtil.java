package com.tianshaokai.common.utils;

import android.text.TextUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

public class NumberFormatUtil {

    private static NumberFormatUtil instance = null;

    private static ConcurrentHashMap<String, DecimalFormat> hashMap = null;

    private NumberFormatUtil() {
        hashMap = new ConcurrentHashMap<>();
    }

    public static NumberFormatUtil getInstance() {
        if (instance == null) {
            synchronized (NumberFormatUtil.class) {
                if (instance == null) {
                    instance = new NumberFormatUtil();
                }
            }
        }
        return instance;
    }

    //    保留一位小数,四舍五入
    public static final String Format_1 = "#.#";
    //    保留两位小数,四舍五入
    public static final String Format_2 = "0.00";
    //    保留两位小数，四射五入，但当末尾位0时，自动忽略
    public static final String Format_3 = "#.##";
    //    格式化分隔数字
    public static final String Format_4 = "#,##,###.####";
    //    按百分制输出,保留两位小数 五舍六入模式
    public static final String Format_5 = "#.##%";
    //    将所有数字加上负号输出,保留两位小数
    public static final String Format_6 = "-#.##";



    /***
     *  保留1位小数去掉四舍五入
     *  RoundingMode.CEILING：取右边最近的整数
     RoundingMode.DOWN：去掉小数部分取整，也就是正数取左边，负数取右边，相当于向原点靠近的方向取整
     RoundingMode.FLOOR：取左边最近的正数
     RoundingMode.HALF_DOWN:五舍六入，负数先取绝对值再五舍六入再负数
     RoundingMode.HALF_UP:四舍五入，负数原理同上
     RoundingMode.HALF_EVEN:这个比较绕，整数位若是奇数则四舍五入，若是偶数则五舍六入
     * @param str 字符串
     * @return 返回format 数据
     */
    public String getFormatNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            DecimalFormat formater = new DecimalFormat();
            formater.setMaximumFractionDigits(1);
            formater.setGroupingSize(0);
            formater.setRoundingMode(RoundingMode.HALF_UP);
            return formater.format(Double.valueOf(str));
        } catch (NumberFormatException e) {
            LogUtil.e("数据转换异常: " + e.toString());
        }
        return "";
    }

    public String getFormatNumber(Double d, String formatType) {
        try {
            DecimalFormat format = hashMap.get(formatType);
            if (format == null) {
                format = new DecimalFormat(formatType);
                hashMap.put(formatType, format);
            }
            return format.format(d);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

}
