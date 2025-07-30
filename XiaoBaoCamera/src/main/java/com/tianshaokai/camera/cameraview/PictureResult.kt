package com.tianshaokai.camera.cameraview

import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import com.tianshaokai.camera.cameraview.controls.Facing
import com.tianshaokai.camera.cameraview.controls.PictureFormat
import com.tianshaokai.camera.cameraview.size.Size
import java.io.File

class PictureResult private constructor(
    val data: ByteArray?,
    val facing: Facing?,
    val format: PictureFormat?,
    val isSnapshot: Boolean,
    val location: Location?,
    val rotation: Int,
    val size: Size?
) {

    class Stub {
        var data: ByteArray? = null
        var facing: Facing? = null
        var format: PictureFormat? = null
        var isSnapshot: Boolean = false
        var location: Location? = null
        var rotation: Int = 0
        var size: Size? = null
    }

    constructor(stub: Stub) : this(
        data = stub.data,
        facing = stub.facing,
        format = stub.format,
        isSnapshot = stub.isSnapshot,
        location = stub.location,
        rotation = stub.rotation,
        size = stub.size
    )

    fun toBitmap(width: Int = -1, height: Int = -1, bitmapCallback: BitmapCallback) {
        when (format) {
            PictureFormat.JPEG -> {
                CameraUtils.decodeBitmap(data, width, height, BitmapFactory.Options(), rotation, bitmapCallback)
            }
            PictureFormat.DNG -> {
                if (Build.VERSION.SDK_INT >= 24) {
                    CameraUtils.decodeBitmap(data, width, height, BitmapFactory.Options(), rotation, bitmapCallback)
                } else {
                    throw UnsupportedOperationException("PictureResult.toBitmap() does not support this picture format: $format")
                }
            }
            else -> throw UnsupportedOperationException("PictureResult.toBitmap() does not support this picture format: $format")
        }
    }

    fun toFile(file: File, fileCallback: FileCallback) {
        CameraUtils.writeToFile(data, file, fileCallback)
    }
}
