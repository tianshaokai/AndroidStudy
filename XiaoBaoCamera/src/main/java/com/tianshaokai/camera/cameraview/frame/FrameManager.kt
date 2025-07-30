package com.tianshaokai.camera.cameraview.frame

import android.graphics.ImageFormat
import com.tianshaokai.camera.cameraview.LOG
import com.tianshaokai.camera.cameraview.engine.offset.Angles
import com.tianshaokai.camera.cameraview.engine.offset.Axis
import com.tianshaokai.camera.cameraview.engine.offset.Reference
import com.tianshaokai.camera.cameraview.size.Size
import java.util.concurrent.LinkedBlockingQueue

abstract class FrameManager<T>(
    private val poolSize: Int,
    private val frameDataClass: Class<T>
) {

    companion object {
//        private val LOG = CameraLogger.create(FrameManager::class.java.simpleName)
        private const val TAG = "FrameManager"
    }

    private var angles: Angles? = null
    private val frameQueue: LinkedBlockingQueue<Frame> = LinkedBlockingQueue(poolSize)
    private var frameBytes: Int = -1
    private var frameSize: Size? = null
    private var frameFormat: Int = -1

    protected abstract fun onCloneFrameData(data: T): T

    protected abstract fun onFrameDataReleased(data: T, wasRecycled: Boolean)

    fun getPoolSize(): Int = poolSize

    fun getFrameBytes(): Int = frameBytes

    fun getFrameDataClass(): Class<T> = frameDataClass

    fun setUp(format: Int, size: Size, angles: Angles) {
        if (isSetUp()) {
            throw IllegalStateException("FrameManager is already set up.")
        }
        frameSize = size
        frameFormat = format
        frameBytes = ((size.getHeight() * size.getWidth() * ImageFormat.getBitsPerPixel(format)) / 8.0).toInt()
        repeat(poolSize) {
            frameQueue.offer(Frame(this))
        }
        this.angles = angles
    }

    protected fun isSetUp(): Boolean = frameSize != null

    fun getFrame(data: T, timestamp: Long): Frame? {
        if (!isSetUp()) {
            throw IllegalStateException("Can't call getFrame() after releasing or before setUp.")
        }
        val frame = frameQueue.poll()
        if (frame != null) {
            LOG.v("getFrame for time:", timestamp, "RECYCLING.")
            frame.setContent(
                data,
                timestamp,
                angles?.offset(Reference.SENSOR, Reference.OUTPUT, Axis.RELATIVE_TO_SENSOR) ?: 0,
                angles?.offset(Reference.SENSOR, Reference.VIEW, Axis.RELATIVE_TO_SENSOR) ?: 0,
                frameSize,
                frameFormat
            )
            return frame
        }
        LOG.i("getFrame for time:", timestamp, "NOT AVAILABLE.")
        onFrameDataReleased(data, false)
        return null
    }

    fun onFrameReleased(frame: Frame, data: T) {
        if (isSetUp()) {
            onFrameDataReleased(data, frameQueue.offer(frame))
        }
    }

    fun cloneFrameData(data: T): T = onCloneFrameData(data)

    fun release() {
        if (!isSetUp()) {
            LOG.w("release called twice. Ignoring.")
            return
        }
        LOG.i("release: Clearing the frame and buffer queue.")
        frameQueue.clear()
        frameBytes = -1
        frameSize = null
        frameFormat = -1
        angles = null
    }
}