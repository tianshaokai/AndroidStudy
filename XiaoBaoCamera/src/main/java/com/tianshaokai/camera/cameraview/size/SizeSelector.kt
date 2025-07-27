package com.tianshaokai.camera.cameraview.size

interface SizeSelector {
    fun select(sizes: List<Size>): List<Size>
}