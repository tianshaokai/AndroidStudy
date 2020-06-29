package com.tianshaokai.study;

import android.app.Application;

import com.tianshaokai.common.utils.CrashHandler;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashHandler.getInstance().init(this);
    }
}
