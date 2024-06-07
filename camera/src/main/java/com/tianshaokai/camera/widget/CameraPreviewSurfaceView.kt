package com.tianshaokai.camera.widget

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.tianshaokai.camera.camera1.basic.Camera1Engine
import com.tianshaokai.camera.type.CameraFacing

class CameraPreviewSurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val TAG = "CameraPreviewSurface"

    private var camera1Engine: Camera1Engine? = null

    init {
        camera1Engine = Camera1Engine().apply {
            setCameraFacing(CameraFacing.BACK)
        }

        holder.addCallback(this)
    }


    override fun surfaceCreated(holder: SurfaceHolder) {
        camera1Engine?.startPreview(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera1Engine?.stopPreview()
    }


}