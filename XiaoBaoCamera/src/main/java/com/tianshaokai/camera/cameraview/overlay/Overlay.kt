package com.tianshaokai.camera.cameraview.overlay

import android.graphics.Canvas

interface Overlay {

    enum class Target {
        PREVIEW,
        PICTURE_SNAPSHOT,
        VIDEO_SNAPSHOT
    }

    fun drawOn(target: Target, canvas: Canvas)

    fun drawsOn(target: Target): Boolean
}