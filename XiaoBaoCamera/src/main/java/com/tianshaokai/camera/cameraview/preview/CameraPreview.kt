package com.tianshaokai.camera.cameraview.preview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import com.tianshaokai.camera.cameraview.size.Size

abstract class CameraPreview<T : View, Output>(context: Context, viewGroup: ViewGroup) {
    private var surfaceCallback: SurfaceCallback? = null
    protected var cropCallback: CropCallback? = null
    protected var cropping: Boolean = false
    protected var drawRotation: Int = 0
    protected var inputStreamWidth: Int = 0
    protected var inputStreamHeight: Int = 0
    protected var outputSurfaceWidth: Int = 0
    protected var outputSurfaceHeight: Int = 0
    private val view: T = onCreateView(context, viewGroup)

    protected interface CropCallback {
        fun onCrop()
    }

    interface SurfaceCallback {
        fun onSurfaceAvailable()
        fun onSurfaceChanged()
        fun onSurfaceDestroyed()
    }

    abstract fun getOutput(): Output
    abstract fun getOutputClass(): Class<Output>
    abstract fun getRootView(): View
    protected abstract fun onCreateView(context: Context, viewGroup: ViewGroup): T

    open fun onPause() {}
    open fun onResume() {}

    open fun supportsCropping(): Boolean = false

    fun setSurfaceCallback(surfaceCallback: SurfaceCallback?) {
        if (hasSurface()) {
            this.surfaceCallback?.onSurfaceDestroyed()
        }
        this.surfaceCallback = surfaceCallback
        if (hasSurface()) {
            surfaceCallback?.onSurfaceAvailable()
        }
    }

    fun getView(): T = view

    fun setStreamSize(width: Int, height: Int) {
       // LOG.i("setStreamSize:", "desiredW=$width", "desiredH=$height")
        inputStreamWidth = width
        inputStreamHeight = height
        if (width > 0 && height > 0) {
            crop(cropCallback)
        }
    }

    fun getStreamSize(): Size = Size(inputStreamWidth, inputStreamHeight)

    fun getSurfaceSize(): Size = Size(outputSurfaceWidth, outputSurfaceHeight)

    fun hasSurface(): Boolean = outputSurfaceWidth > 0 && outputSurfaceHeight > 0

    protected fun dispatchOnSurfaceAvailable(width: Int, height: Int) {
//        LOG.i("dispatchOnSurfaceAvailable:", "w=$width", "h=$height")
        outputSurfaceWidth = width
        outputSurfaceHeight = height
        if (width > 0 && height > 0) {
            crop(cropCallback)
        }
        surfaceCallback?.onSurfaceAvailable()
    }

    protected fun dispatchOnSurfaceSizeChanged(width: Int, height: Int) {
//        LOG.i("dispatchOnSurfaceSizeChanged:", "w=$width", "h=$height")
        if (width == outputSurfaceWidth && height == outputSurfaceHeight) return
        outputSurfaceWidth = width
        outputSurfaceHeight = height
        if (width > 0 && height > 0) {
            crop(cropCallback)
        }
        surfaceCallback?.onSurfaceChanged()
    }

    protected fun dispatchOnSurfaceDestroyed() {
        outputSurfaceWidth = 0
        outputSurfaceHeight = 0
        surfaceCallback?.onSurfaceDestroyed()
    }

    fun onDestroy() {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            onDestroyView()
        } else {
            val handler = Handler(Looper.getMainLooper())
            val taskCompletionSource = TaskCompletionSource<Void>()
            handler.post {
                onDestroyView()
                taskCompletionSource.setResult(null)
            }
            try {
                Tasks.await(taskCompletionSource.task)
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    protected open fun onDestroyView() {
        val rootView = getRootView()
        val parent = rootView.parent
        if (parent is ViewGroup) {
            parent.removeView(rootView)
        }
    }

    protected open fun crop(cropCallback: CropCallback?) {
        cropCallback?.onCrop()
    }

    fun isCropping(): Boolean = cropping

    fun setDrawRotation(rotation: Int) {
        drawRotation = rotation
    }
}