package com.tianshaokai.camera.cameraview.engine.offset

import com.tianshaokai.camera.cameraview.LOG
import com.tianshaokai.camera.cameraview.controls.Facing

class Angles {

    companion object {
//        private val LOG = CameraLogger.create(Angles::class.java.simpleName)
        private const val TAG = "Angles"
    }

    private var sensorFacing: Facing? = null
    var sensorOffset: Int = 0

    var displayOffset: Int = 0

    var deviceOrientation: Int = 0


    fun setSensorOffset(facing: Facing, offset: Int) {
        sanitizeInput(offset)
        sensorFacing = facing
        sensorOffset = if (facing == Facing.FRONT) {
            sanitizeOutput(360 - offset)
        } else {
            offset
        }
        print()
    }

    fun setDisplayOffset(offset: Int) {
        sanitizeInput(offset)
        displayOffset = offset
        print()
    }

    fun setDeviceOrientation(orientation: Int) {
        sanitizeInput(orientation)
        deviceOrientation = orientation
        print()
    }

    private fun print() {
        LOG.i(
            "Angles changed:",
            "sensorOffset: $sensorOffset",
            "displayOffset: $displayOffset",
            "deviceOrientation: $deviceOrientation"
        )
    }

    fun offset(reference: Reference, reference2: Reference, axis: Axis): Int {
        val absoluteOffset = absoluteOffset(reference, reference2)
        return if (axis == Axis.RELATIVE_TO_SENSOR && sensorFacing == Facing.FRONT) {
            sanitizeOutput(360 - absoluteOffset)
        } else {
            absoluteOffset
        }
    }

    private fun absoluteOffset(reference: Reference, reference2: Reference): Int {
        if (reference == reference2) return 0

        if (reference2 == Reference.BASE) {
            return sanitizeOutput(360 - absoluteOffset(reference2, reference))
        }

        if (reference == Reference.BASE) {
            return when (reference2) {
                Reference.VIEW -> sanitizeOutput(360 - displayOffset)
                Reference.OUTPUT -> sanitizeOutput(deviceOrientation)
                Reference.SENSOR -> sanitizeOutput(360 - sensorOffset)
                else -> throw RuntimeException("Unknown reference: $reference2")
            }
        }

        return sanitizeOutput(
            absoluteOffset(Reference.BASE, reference2) - absoluteOffset(Reference.BASE, reference)
        )
    }

    fun flip(reference: Reference, reference2: Reference): Boolean {
        return offset(reference, reference2, Axis.ABSOLUTE) % 180 != 0
    }

    private fun sanitizeInput(value: Int) {
        if (value != 0 && value != 90 && value != 180 && value != 270) {
            throw IllegalStateException("This value is not sanitized: $value")
        }
    }

    private fun sanitizeOutput(value: Int): Int {
        return (value + 360) % 360
    }
}
