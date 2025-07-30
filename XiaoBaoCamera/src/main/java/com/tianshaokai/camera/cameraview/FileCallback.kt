package com.tianshaokai.camera.cameraview

import java.io.File

interface FileCallback {
    fun onFileReady(file: File?)
}