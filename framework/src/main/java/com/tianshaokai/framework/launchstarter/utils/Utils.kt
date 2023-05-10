package com.tianshaokai.framework.launchstarter.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

object Utils {
    private var sCurProcessName: String? = null

    fun isMainProcess(context: Context): Boolean {
        val processName = getCurProcessName(context)
        return if (processName != null && processName.contains(":")) {
            false
        } else processName != null && processName == context.packageName
    }

    private fun getCurProcessName(context: Context): String? {
        val procName = sCurProcessName
        if (procName.isNullOrEmpty().not()) {
            return procName
        }
        try {
            val pid = Process.myPid()
            val mActivityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in mActivityManager.runningAppProcesses) {
                if (appProcess.pid == pid) {
                    sCurProcessName = appProcess.processName
                    return sCurProcessName
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        sCurProcessName = getCurProcessNameFromProc()
        return sCurProcessName
    }

    private fun getCurProcessNameFromProc(): String? {
        var cmdlineReader: BufferedReader? = null
        try {
            cmdlineReader = BufferedReader(
                InputStreamReader(
                    FileInputStream(
                        "/proc/" + Process.myPid() + "/cmdline"
                    ),
                    "iso-8859-1"
                )
            )
            var c: Int
            val processName = StringBuilder()
            while (cmdlineReader.read().also { c = it } > 0) {
                processName.append(c.toChar())
            }
            return processName.toString()
        } catch (e: Throwable) { // ignore
        } finally {
            if (cmdlineReader != null) {
                try {
                    cmdlineReader.close()
                } catch (e: java.lang.Exception) { // ignore
                }
            }
        }
        return null
    }
}