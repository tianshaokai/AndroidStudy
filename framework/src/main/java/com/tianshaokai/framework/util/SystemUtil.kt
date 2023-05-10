package com.tianshaokai.framework.util

import android.os.Build

object SystemUtil {

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    fun getSystemModel(): String {
        return Build.MODEL
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    fun getDeviceBrand(): String {
        return Build.BRAND
    }

    /**
     * 是否为鸿蒙系统
     *
     * @return true为鸿蒙系统
     */
    fun isHarmonyOs(): Boolean {
        return try {
            val buildExClass = Class.forName("com.huawei.system.BuildEx")
            val osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass)
            "Harmony".equals(osBrand?.toString(), ignoreCase = true)
        } catch (x: Throwable) {
            false
        }
    }
}