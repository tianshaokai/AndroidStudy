package com.tianshaokai.camera.cameraview.frame

import com.tianshaokai.camera.cameraview.LOG
import com.tianshaokai.camera.cameraview.size.Size

class Frame(private val manager: FrameManager<*>) {

    companion object {
//        private val LOG = CameraLogger.create(Frame::class.java.simpleName)
        private const val TAG = "Frame"
    }

    private val dataClass: Class<*> = manager.getFrameDataClass()
    private var data: Any? = null
    private var time: Long = -1
    private var lastTime: Long = -1
    private var userRotation: Int = 0
    private var viewRotation: Int = 0
    private var size: Size? = null
    private var format: Int = -1

    fun setContent(
        data: Any?,
        time: Long,
        userRotation: Int,
        viewRotation: Int,
        size: Size?,
        format: Int
    ) {
        this.data = data
        this.time = time
        this.lastTime = time
        this.userRotation = userRotation
        this.viewRotation = viewRotation
        this.size = size
        this.format = format
    }

    private fun hasContent(): Boolean {
        return data != null
    }

    private fun ensureHasContent() {
        if (!hasContent()) {
            LOG.e("Frame is dead! time:", time, "lastTime:", lastTime)
            throw RuntimeException(
                "You should not access a released frame. If this frame was passed to a FrameProcessor, " +
                        "you can only use its contents synchronously, for the duration of the process() method."
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is Frame && other.time == this.time
    }

    fun freeze(): Frame {
        ensureHasContent()
        val frozenFrame = Frame(manager)
        frozenFrame.setContent(
            manager.cloneFrameData(getData()),
            time,
            userRotation,
            viewRotation,
            size,
            format
        )
        return frozenFrame
    }

    fun release() {
        if (hasContent()) {
            LOG.v("Frame with time", time, "is being released.")
            val releasedData = data
            data = null
            userRotation = 0
            viewRotation = 0
            time = -1
            size = null
            format = -1
            manager.onFrameReleased(this, releasedData)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(): T {
        ensureHasContent()
        return data as T
    }

    fun getDataClass(): Class<*> = dataClass

    fun getTime(): Long {
        ensureHasContent()
        return time
    }

    @Deprecated("Use getRotationToUser() instead.")
    fun getRotation(): Int {
        return getRotationToUser()
    }

    fun getRotationToUser(): Int {
        ensureHasContent()
        return userRotation
    }

    fun getRotationToView(): Int {
        ensureHasContent()
        return viewRotation
    }

    fun getSize(): Size? {
        ensureHasContent()
        return size
    }

    fun getFormat(): Int {
        ensureHasContent()
        return format
    }
}
