package com.tianshaokai.camera.cameraview

import android.graphics.Bitmap

interface BitmapCallback {
    fun onBitmapReady(bitmap: Bitmap?)
}