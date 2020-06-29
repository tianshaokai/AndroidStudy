package com.tianshaokai.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.pm.PackageInfoCompat;

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
        if (context == null) {
            return null;
        }
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            return manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

}
