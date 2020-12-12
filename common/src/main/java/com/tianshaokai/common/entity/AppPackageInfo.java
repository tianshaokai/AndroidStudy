package com.tianshaokai.common.entity;

import android.content.pm.PackageInfo;

public class AppPackageInfo {

    private PackageInfo packageInfo;
    private long appSize;

    public AppPackageInfo(PackageInfo packageInfo, long appSize) {
        this.packageInfo = packageInfo;
        this.appSize = appSize;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }
}
