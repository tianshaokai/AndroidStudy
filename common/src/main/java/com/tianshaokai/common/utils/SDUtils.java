package com.tianshaokai.common.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class SDUtils {

    public static boolean isSdMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * @return 获取外部相册路径
     */
    public static String getStorageDCIMPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    /**
     * @return 获取外部音频路径
     */
    public static String getStorageAudioPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    /**
     * @return 获取外部视频路径
     */
    public static String getStorageVideoPath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }


    public static String getExternalFilesDir(Context context, String dir) {
        if (context == null) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(dir);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageDCIMPath(Context context) {
        if (context == null) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageAudioPath(Context context) {
        if (context == null || !isSdMounted()) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageMoviePath(Context context) {
        if (context == null) {
            return "";
        }
        File filePath = context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (filePath != null) {
            return filePath.getAbsolutePath();
        }
        return "";
    }

    public static String getPackageCrashPath(Context context) {
        return getExternalFilesDir(context, "Crash");
    }

    /**
     * 未使用磁盘空间
     * @return 返回剩余空间
     */
    public static long getFreeDiskSpace() {
        if (isSdMounted()) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            return availCount * blockSize;
        }
        return 0;
    }

}
