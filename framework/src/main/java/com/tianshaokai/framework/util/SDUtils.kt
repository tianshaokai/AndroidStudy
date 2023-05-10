package com.tianshaokai.framework.util

import android.content.Context
import android.os.Environment
import android.os.StatFs

object SDUtils {

    fun isSdMounted(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    fun getExternalFilesDir(context: Context, dir: String): String {
        if (!isSdMounted()) {
            return ""
        }
        val filePath = context.applicationContext.getExternalFilesDir(dir)
        return if (filePath != null) {
            filePath.absolutePath
        } else ""
    }

    fun getPackageDCIMPath(context: Context): String {
       return getExternalFilesDir(context, Environment.DIRECTORY_DCIM)
    }

    fun getPackageAudioPath(context: Context): String {
        return getExternalFilesDir(context, Environment.DIRECTORY_MUSIC)
    }

    fun getPackageMoviePath(context: Context): String {
        return getExternalFilesDir(context, Environment.DIRECTORY_MOVIES)
    }

    fun getPackageCrashPath(context: Context): String {
        return getExternalFilesDir(context, "Crash")
    }

    /**
     * 获取剩余存储，有外部取外部，没有取内部
     * 返回 字节 Bytes
     */
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
    fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return totalBlocks * blockSize
    }
}