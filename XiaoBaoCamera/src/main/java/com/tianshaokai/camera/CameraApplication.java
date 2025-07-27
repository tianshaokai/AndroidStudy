package com.tianshaokai.camera;

import android.app.Application;

import com.triversoft.common.TypeFaceUtil;

public class CameraApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        CrashHandler.getInstance().init(this);

        TypeFaceUtil.Companion.getInstance().initTypeFace(this);
    }
}
