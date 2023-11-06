package com.tianshaokai.framework.util

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.text.DecimalFormat

object FileUtils {

    /**
     * 读取文件内容
     */
    @JvmStatic
    fun getFileContent(path: String?): String {
        var fileReader: FileReader? = null
        var bufferReader: BufferedReader? = null
        try {
            fileReader = FileReader(path)
            bufferReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()
            var str: String?
            while (bufferReader.readLine().also { str = it } != null) {
                stringBuilder.append(str)
            }
            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                fileReader?.close()
                bufferReader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    /**
     * 转换文件大小
     *
     * @param fileSize
     * @return
     */
    @JvmStatic
    fun formatFileSize(fileSize: Long): String {
        val df = DecimalFormat("#.00")
        var fileSizeString = ""
        val wrongSize = "0B"
        if (fileSize == 0L) {
            return wrongSize
        }
        fileSizeString = if (fileSize < 1024) {
            df.format(fileSize.toDouble()) + "B"
        } else if (fileSize < 1048576) {
            df.format(fileSize.toDouble() / 1024) + "KB"
        } else if (fileSize < 1073741824) {
            df.format(fileSize.toDouble() / 1048576) + "MB"
        } else {
            df.format(fileSize.toDouble() / 1073741824) + "GB"
        }
        return fileSizeString
    }

}