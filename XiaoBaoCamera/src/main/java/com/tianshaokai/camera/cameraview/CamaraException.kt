package com.tianshaokai.camera.cameraview

class CameraException : RuntimeException {

    companion object {
        const val REASON_UNKNOWN = 0
        const val REASON_FAILED_TO_CONNECT = 1
        const val REASON_FAILED_TO_START_PREVIEW = 2
        const val REASON_DISCONNECTED = 3
        const val REASON_PICTURE_FAILED = 4
        const val REASON_VIDEO_FAILED = 5
        const val REASON_NO_CAMERA = 6
    }

    val reason: Int

    constructor(cause: Throwable) : super(cause) {
        this.reason = REASON_UNKNOWN
    }

    constructor(cause: Throwable, reason: Int) : super(cause) {
        this.reason = reason
    }

    constructor(reason: Int) : super() {
        this.reason = reason
    }

    fun isUnrecoverable(): Boolean {
        return reason == REASON_FAILED_TO_CONNECT || reason == REASON_FAILED_TO_START_PREVIEW || reason == REASON_DISCONNECTED
    }
}
