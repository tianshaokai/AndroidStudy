package com.tianshaokai.common.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.core.content.pm.PackageInfoCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppUtil {

    private static volatile AppUtil instance = null;

    private AppUtil() {
    }

    public static AppUtil getInstance() {
        if (instance == null) {
            synchronized (AppUtil.class) {
                if (instance == null) {
                    instance = new AppUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 获取PackageInfo
     * @param context 上下文
     * @return 返回 app 信息
     */
    public PackageInfo getPackageInfo(Context context) {
        return getPackageInfo(context, context.getPackageName());
    }

    public long getVersionCode(Context context) {
        if (context == null) {
            return 0;
        }
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return 0;
        }
        return PackageInfoCompat.getLongVersionCode(packageInfo);
    }

    public String getVersionName(Context context) {
        if (context == null) {
            return "";
        }
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo == null) {
            return "";
        }
        return packageInfo.versionName;
    }

    /**
     * 获取已经安装应用
     * @param context       上下文
     * @return 返回已经安装应用
     */
    public List<PackageInfo> getInstalledPackages(Context context) {
        if (context == null) {
            return Collections.emptyList();
        }
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES |
                PackageManager.GET_SERVICES);
        if(packageInfoList == null || packageInfoList.isEmpty()) return Collections.emptyList();
        List<PackageInfo> packageInfoAppList = new ArrayList<>();
        for (int i = 0, length = packageInfoList.size(); i < length; i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            if (isSystemApp(packageInfo)) {
                continue;
            }
            packageInfoAppList.add(packageInfo);
        }
        return packageInfoAppList;
    }

    // 通过packName得到PackageInfo，作为参数传入即可
    public boolean isSystemApp(PackageInfo pi) {
        return (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
    }

    /**
     * 应用是否安装
     * @param context       上下文
     * @param packageName   包名
     * @return 返回应用是否已经安装
     */
    public boolean isInstalled(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if(packageInfo == null) return false;
        return true;
    }


    /**
     * 获取应用包名信息
     * @param context       上下文
     * @param packageName   包名
     * @return 获取应用信息
     */
    public PackageInfo getPackageInfo(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                return packageInfo;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
