package com.tianshaokai.framework.util

import android.annotation.SuppressLint
import android.os.Build

object SystemUtil {

    /**
     * 获取当前手机系统版本号
     * Android 版本号
     * @return 系统版本号
     */
    @JvmStatic
    fun getSystemVersion(): String {
        return Build.VERSION.RELEASE
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    @JvmStatic
    fun getSystemModel(): String {
        return Build.MODEL
    }

    /**
     * 获取设备品牌型号
     */
    @JvmStatic
    fun getSystemBrandModel(): String {
        when ("") {

            "huawei"-> return getSystemPropertyValue("ro.config.marketing_name")

            "Oppo" -> return getSystemPropertyValue("ro.vendor.oplus.market.name")

            else -> return ""
        }
    }

    @JvmStatic
    fun getSystemOsVersion(): String {
        if (isHarmonyOs()) {
            return "HarmonyOS " + getSystemPropertyValue("hw_sc.build.platform.version")
        }
        return "Android " + getSystemVersion()
    }

    @JvmStatic
    fun getSystemModelOsVersion(): String {
//        return getSystemPropertyValue("ro.huawei.build.display.id")
        val a = getSystemPropertyValue("ro.build.version.oplusrom")
        val b = getSystemPropertyValue("ro.build.display.ota")
        return a + b
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


    @SuppressLint("PrivateApi")
    private fun getSystemPropertyValue(key: String): String {
        return try {
            val classType = Class.forName("android.os.SystemProperties")
            val getMethod = classType.getDeclaredMethod("get", String::class.java)
            getMethod.invoke(classType, key) as String
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}