package com.tianshaokai.app.utils;

import android.os.Build;
import android.util.Log;

import com.tianshaokai.app.BuildConfig;

public class Logger {

    public static void d(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.e(tag, msg);
    }
}
