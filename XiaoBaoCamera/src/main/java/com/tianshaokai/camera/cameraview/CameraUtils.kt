package com.tianshaokai.camera.cameraview

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import com.tianshaokai.camera.cameraview.controls.Facing
import com.tianshaokai.camera.cameraview.internal.WorkerHandler
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object CameraUtils {
//    private val LOG = CameraLogger.create(CameraUtils::class.java.simpleName)
    private const val TAG = "CameraUtils"

    fun hasCameras(context: Context): Boolean {
        val packageManager = context.packageManager
        return packageManager.hasSystemFeature("android.hardware.camera") ||
                packageManager.hasSystemFeature("android.hardware.camera.front")
    }

    fun hasCameraFacing(context: Context, facing: Facing): Boolean {
        val mappedFacing = Camera1Mapper.get().mapFacing(facing)
        val cameraInfo = android.hardware.Camera.CameraInfo()
        val numberOfCameras = android.hardware.Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            android.hardware.Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == mappedFacing) {
                return true
            }
        }
        return false
    }

    fun writeToFile(data: ByteArray, file: File): File? {
        if (file.exists() && !file.delete()) {
            return null
        }
        return kotlin.runCatching {
            BufferedOutputStream(FileOutputStream(file)).use { outputStream ->
                outputStream.write(data)
                outputStream.flush()
            }
            file
        }.getOrNull()
    }

    fun writeToFile(data: ByteArray, file: File, fileCallback: FileCallback) {
        val handler = Handler()
        WorkerHandler.execute(Runnable {
            val resultFile = writeToFile(data, file)
            handler.post { fileCallback.onFileReady(resultFile) }
        })
    }

    fun decodeBitmap(data: ByteArray): Bitmap? {
        return decodeBitmap(data, Int.MAX_VALUE, Int.MAX_VALUE)
    }

    fun decodeBitmap(data: ByteArray, bitmapCallback: BitmapCallback) {
        decodeBitmap(data, Int.MAX_VALUE, Int.MAX_VALUE, bitmapCallback)
    }

    fun decodeBitmap(data: ByteArray, width: Int, height: Int, bitmapCallback: BitmapCallback) {
        decodeBitmap(data, width, height, BitmapFactory.Options(), bitmapCallback)
    }

    fun decodeBitmap(data: ByteArray, width: Int, height: Int, options: BitmapFactory.Options, bitmapCallback: BitmapCallback) {
        decodeBitmap(data, width, height, options, -1, bitmapCallback)
    }

    private fun decodeBitmap(
        data: ByteArray,
        width: Int,
        height: Int,
        options: BitmapFactory.Options,
        rotation: Int,
        bitmapCallback: BitmapCallback
    ) {
        val handler = Handler()
        WorkerHandler.execute(Runnable {
            val bitmap = decodeBitmap(data, width, height, options, rotation)
            handler.post { bitmapCallback.onBitmapReady(bitmap) }
        })
    }

    fun decodeBitmap(data: ByteArray, width: Int, height: Int): Bitmap? {
        return decodeBitmap(data, width, height, BitmapFactory.Options())
    }

    fun decodeBitmap(data: ByteArray, width: Int, height: Int, options: BitmapFactory.Options): Bitmap? {
        return decodeBitmap(data, width, height, options, -1)
    }

    fun decodeBitmap(data: ByteArray, width: Int, height: Int, options: BitmapFactory.Options, rotation: Int): Bitmap? {
        return kotlin.runCatching {
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
            val sampleSize = computeSampleSize(options.outWidth, options.outHeight, width, height)
            options.inSampleSize = sampleSize
            options.inJustDecodeBounds = false
            var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, options)
            if (rotation != -1) {
                val matrix = android.graphics.Matrix()
                matrix.postRotate(rotation.toFloat())
                bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }
            bitmap
        }.onFailure { e ->
            if (e is OutOfMemoryError) {
//                LOG.e(TAG, "decodeBitmap: OutOfMemoryError", e)
            }
        }.getOrNull()
    }

    private fun computeSampleSize(outWidth: Int, outHeight: Int, reqWidth: Int, reqHeight: Int): Int {
        var sampleSize = 1
        if (outHeight > reqHeight || outWidth > reqWidth) {
            while (outHeight / sampleSize >= reqHeight && outWidth / sampleSize >= reqWidth) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }
}
