package com.tianshaokai.camera.cameraview

import android.location.Location
import com.tianshaokai.camera.cameraview.controls.Audio
import com.tianshaokai.camera.cameraview.controls.Facing
import com.tianshaokai.camera.cameraview.controls.VideoCodec
import com.tianshaokai.camera.cameraview.size.Size
import java.io.File
import java.io.FileDescriptor

class VideoResult private constructor(
    val isSnapshot: Boolean,
    val location: Location?,
    val rotation: Int,
    val size: Size?,
    val file: File?,
    val fileDescriptor: FileDescriptor?,
    val facing: Facing?,
    val videoCodec: VideoCodec?,
    val audio: Audio?,
    val maxSize: Long,
    val maxDuration: Int,
    val endReason: Int,
    val videoBitRate: Int,
    val videoFrameRate: Int,
    val audioBitRate: Int
) {

    companion object {
        const val REASON_USER = 0
        const val REASON_MAX_SIZE_REACHED = 1
        const val REASON_MAX_DURATION_REACHED = 2
    }

    class Stub {
        var audio: Audio? = null
        var audioBitRate: Int = 0
        var endReason: Int = 0
        var facing: Facing? = null
        var file: File? = null
        var fileDescriptor: FileDescriptor? = null
        var isSnapshot: Boolean = false
        var location: Location? = null
        var maxDuration: Int = 0
        var maxSize: Long = 0
        var rotation: Int = 0
        var size: Size? = null
        var videoBitRate: Int = 0
        var videoCodec: VideoCodec? = null
        var videoFrameRate: Int = 0
    }

    constructor(stub: Stub) : this(
        isSnapshot = stub.isSnapshot,
        location = stub.location,
        rotation = stub.rotation,
        size = stub.size,
        file = stub.file,
        fileDescriptor = stub.fileDescriptor,
        facing = stub.facing,
        videoCodec = stub.videoCodec,
        audio = stub.audio,
        maxSize = stub.maxSize,
        maxDuration = stub.maxDuration,
        endReason = stub.endReason,
        videoBitRate = stub.videoBitRate,
        videoFrameRate = stub.videoFrameRate,
        audioBitRate = stub.audioBitRate
    )

    fun getFile(): File {
        return file ?: throw RuntimeException("File is only available when takeVideo(File) is used.")
    }

    fun getFileDescriptor(): FileDescriptor {
        return fileDescriptor ?: throw RuntimeException("FileDescriptor is only available when takeVideo(FileDescriptor) is used.")
    }
}
