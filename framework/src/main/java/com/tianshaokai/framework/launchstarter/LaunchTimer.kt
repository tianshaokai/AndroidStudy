package com.tianshaokai.framework.launchstarter

import android.util.Log

object LaunchTimer {

    private var sTime: Long = 0

    @JvmStatic
    fun startRecord() {
        sTime = System.currentTimeMillis()
    }

    @JvmStatic
    fun endRecord() {
        endRecord("启动耗时")
    }

    @JvmStatic
    fun endRecord(msg: String) {
        val cost = System.currentTimeMillis() - sTime
        Log.i("LaunchTimer", "$msg 消费时长 $cost")
    }
}