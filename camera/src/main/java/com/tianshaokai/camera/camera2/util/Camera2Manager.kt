package com.tianshaokai.camera.camera2.util

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.tianshaokai.camera.type.CameraFacing

object Camera2Manager {
    
    fun getCameraId(facing: CameraFacing): String {
        val context = getContext()
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        
        return manager.cameraIdList.find { id ->
            val characteristics = manager.getCameraCharacteristics(id)
            val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
            when (facing) {
//                CameraFacing.BACK -> facing == CameraCharacteristics.LENS_FACING_BACK
//                CameraFacing.FRONT -> facing == CameraCharacteristics.LENS_FACING_FRONT
                else -> false
            }
        } ?: manager.cameraIdList[0]
    }
    
    fun getSupportedSizes(context: Context, cameraId: String): List<android.util.Size> {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = manager.getCameraCharacteristics(cameraId)
        val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        return map?.getOutputSizes(android.graphics.ImageFormat.JPEG)?.toList() ?: emptyList()
    }
    
    fun getOptimalSize(supportedSizes: List<android.util.Size>, width: Int, height: Int): android.util.Size {
        val targetRatio = width.toFloat() / height
        var optimalSize: android.util.Size? = null
        var minDiff = Float.MAX_VALUE
        
        for (size in supportedSizes) {
            val ratio = size.width.toFloat() / size.height
            if (Math.abs(ratio - targetRatio) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(ratio - targetRatio)
            }
        }
        
        return optimalSize ?: supportedSizes[0]
    }
    
    private fun getContext(): Context {
        // 这里需要实现获取 Context 的逻辑
        // 可以通过依赖注入或其他方式获取
        throw IllegalStateException("Context must be provided")
    }
} 