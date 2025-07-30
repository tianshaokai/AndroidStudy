package com.tianshaokai.camera.cameraview.engine.orchestrator

enum class CameraState(private val state: Int) {
    OFF(0),
    ENGINE(1),
    BIND(2),
    PREVIEW(3);

    fun isAtLeast(cameraState: CameraState): Boolean {
        return this.state >= cameraState.state
    }
}