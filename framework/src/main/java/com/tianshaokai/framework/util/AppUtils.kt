package com.tianshaokai.framework.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.util.*
import java.util.Collections.emptyList

object AppUtils {

    /**
     * 获取包名信息
     */
    @JvmStatic
    fun getPackageName(content: Context): String {
        return try {
            content.packageName
        } catch (var2: Exception) {
            ""
        }
    }

    /**
     * 获取版本信息
     */
    @JvmStatic
    fun getAppVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (ex: PackageManager.NameNotFoundException) {
            ex.printStackTrace()
            "1.0.0"
        }
    }

    @JvmStatic
    fun getAppVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 是否是系统app
     */
    @JvmStatic
    fun isSystemApp(packageInfo: PackageInfo): Boolean {
        return packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
    }

    /**
     * 应用是否安装
     * @param context       上下文
     * @param packageName   包名
     * @return 返回应用是否已经安装
     */
    @JvmStatic
    fun isInstalled(context: Context, packageName: String): Boolean {
        return getPackageInfo(context, packageName) != null
    }

    /**
     * 获取应用包名信息
     * @param context       上下文
     * @param packageName   包名
     * @return 获取应用信息
     */
    @JvmStatic
    fun getPackageInfo(context: Context, packageName: String): PackageInfo? {
        return try {
            return context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取已经安装应用
     * Android 11 添加了包可见性 需要申请权限
     * @see android.Manifest.permission.QUERY_ALL_PACKAGES
     *
     * @param context       上下文
     * @return 返回已经安装应用
     */
    @SuppressLint("QueryPermissionsNeeded")
    @JvmStatic
    fun getInstalledPackages(context: Context): List<PackageInfo> {
        val packageInfoList =
            context.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
        if (packageInfoList.isEmpty()) return emptyList<PackageInfo>()
        val packageInfoAppList: MutableList<PackageInfo> = ArrayList()
        for (packageInfo in packageInfoList) {
            if (isSystemApp(packageInfo)) {
                continue
            }
            packageInfoAppList.add(packageInfo)
        }
        return packageInfoAppList
    }

}