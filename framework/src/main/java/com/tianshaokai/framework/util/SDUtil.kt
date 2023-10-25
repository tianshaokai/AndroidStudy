package com.tianshaokai.framework.util

import android.content.Context
import android.os.Environment
import android.os.StatFs

object SDUtil {

    @JvmStatic
    fun isSdMounted(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    @JvmStatic
    fun getExternalFilesDir(context: Context, dir: String): String {
        if (!isSdMounted()) {
            return ""
        }
        val file = context.applicationContext.getExternalFilesDir(dir)
        return if (file != null) {
            file.absolutePath
        } else ""
    }

    @JvmStatic
    fun getPackageDCIMPath(context: Context): String {
       return getExternalFilesDir(context, Environment.DIRECTORY_DCIM)
    }

    @JvmStatic
    fun getPackageAudioPath(context: Context): String {
        return getExternalFilesDir(context, Environment.DIRECTORY_MUSIC)
    }

    @JvmStatic
    fun getPackageVideoPath(context: Context): String {
        return getExternalFilesDir(context, Environment.DIRECTORY_MOVIES)
    }

    @JvmStatic
    fun getPackageCrashPath(context: Context): String {
        return getExternalFilesDir(context, "Crash")
    }

    /**
     * 获取剩余存储，有外部取外部，没有取内部
     * 返回 字节 Bytes
     */
    @JvmStatic
    fun getMemorySize(): Long {
        return if (isSdMounted()) {
            getAvailableExternalMemorySize()
        } else {
            getAvailableInternalMemorySize()
        }
    }

    /**
     * 获取sdcard剩余存储空间
     */
    @JvmStatic
    fun getAvailableExternalMemorySize(): Long {
        return if (isSdMounted()) {
            try {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSizeLong
                val availableBlocks = stat.availableBlocksLong
                availableBlocks * blockSize
            } catch (e: Exception) {
                0L
            }
        } else {
            0L
        }
    }

    /**
     * 获取手机内部剩余存储空间
     */
    @JvmStatic
    fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return availableBlocks * blockSize
    }

    /**
     * 获取手机内部总的存储空间
     */
    @JvmStatic
    fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return totalBlocks * blockSize
    }
}