package com.tianshaokai.framework.launchstarter.utils

import android.util.Log
object DispatcherLog {
    private var sDebug = true

    fun i(msg: String) {
        if (!sDebug) {
            return
        }
        Log.i("task", msg)
    }

    fun isDebug(): Boolean {
        return sDebug
    }

    fun setDebug(debug: Boolean) {
        sDebug = debug
    }
}