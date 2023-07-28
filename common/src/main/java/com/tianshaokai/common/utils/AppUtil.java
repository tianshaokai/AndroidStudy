package com.tianshaokai.common.utils;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.tianshaokai.common.entity.AppPackageInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<AppPackageInfo> getInstallPackageSizeList(Context context, List<PackageInfo> packageInfoList) {
        StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();

        List<AppPackageInfo> appPackageInfoList = new ArrayList<>();

        for (StorageVolume storageVolume : storageVolumeList) {
            String uuidStr = storageVolume.getUuid();
            UUID uuid = TextUtils.isEmpty(uuidStr) ? StorageManager.UUID_DEFAULT : UUID.fromString(uuidStr);
            for (PackageInfo packageInfo : packageInfoList) {

                try {
                    StorageStats storageStats = storageStatsManager.queryStatsForPackage(uuid, packageInfo.packageName, Process.myUserHandle());
                    long appSize = storageStats.getAppBytes();
//                    long appSize = storageStats.getAppBytes() + storageStats.getCacheBytes() + storageStats.getDataBytes();

                    appPackageInfoList.add(new AppPackageInfo(packageInfo, appSize));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return appPackageInfoList;
    }
}
