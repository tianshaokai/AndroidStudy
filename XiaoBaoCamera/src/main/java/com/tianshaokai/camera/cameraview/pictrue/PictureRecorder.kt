package com.tianshaokai.camera.cameraview.pictrue

import com.tianshaokai.camera.cameraview.PictureResult

abstract class PictureRecorder(
    protected var result: PictureResult.Stub,
    protected var listener: PictureResultListener?
) {

    protected var error: Exception? = null

    interface PictureResultListener {
        fun onPictureResult(result: PictureResult.Stub?, error: Exception?)
        fun onPictureShutter(success: Boolean)
    }

    abstract fun take()

    protected fun dispatchOnShutter(success: Boolean) {
        listener?.onPictureShutter(success)
    }

    protected fun dispatchResult() {
        listener?.onPictureResult(result, error)
        listener = null
    }
}
