package com.tianshaokai.study;

import android.app.Application;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashHandler.getInstance().init(this);
    }
}
