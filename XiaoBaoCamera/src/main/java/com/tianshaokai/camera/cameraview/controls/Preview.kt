package com.tianshaokai.camera.cameraview.controls

enum class Preview(val value: Int) : Control {
    SURFACE(0),
    TEXTURE(1),
    GL_SURFACE(2);

    companion object {
        val DEFAULT: Preview = GL_SURFACE

        fun fromValue(value: Int): Preview {
            return values().find { it.value == value } ?: DEFAULT
        }
    }
}
