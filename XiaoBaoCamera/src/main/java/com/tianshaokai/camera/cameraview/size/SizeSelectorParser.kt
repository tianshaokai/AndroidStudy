package com.tianshaokai.camera.cameraview.size

import android.content.res.TypedArray
import com.tianshaokai.camera.R

class SizeSelectorParser(typedArray: TypedArray) {

    val pictureSizeSelector: SizeSelector by lazy {
        val selectors = mutableListOf<SizeSelector>()

        if (typedArray.hasValue(R.styleable.CameraView_cameraPictureSizeMinWidth)) {
            selectors.add(SizeSelectors.minWidth(typedArray.getInteger(R.styleable.CameraView_cameraPictureSizeMinWidth, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraPictureSizeMaxWidth)) {
            selectors.add(SizeSelectors.maxWidth(typedArray.getInteger(R.styleable.CameraView_cameraPictureSizeMaxWidth, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraPictureSizeMinHeight)) {
            selectors.add(SizeSelectors.minHeight(typedArray.getInteger(R.styleable.CameraView_cameraPictureSizeMinHeight, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraPictureSizeMaxHeight)) {
            selectors.add(SizeSelectors.maxHeight(typedArray.getInteger(R.styleable.CameraView_cameraPictureSizeMaxHeight, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraPictureSizeMinArea)) {
            selectors.add(SizeSelectors.minArea(typedArray.getInteger(R.styleable.CameraView_cameraPictureSizeMinArea, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraPictureSizeMaxArea)) {
            selectors.add(SizeSelectors.maxArea(typedArray.getInteger(R.styleable.CameraView_cameraPictureSizeMaxArea, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraPictureSizeAspectRatio)) {
            selectors.add(SizeSelectors.aspectRatio(AspectRatio.parse(typedArray.getString(R.styleable.CameraView_cameraPictureSizeAspectRatio) ?: ""), 0.0f))
        }
        if (typedArray.getBoolean(R.styleable.CameraView_cameraPictureSizeSmallest, false)) {
            selectors.add(SizeSelectors.smallest())
        }
        if (typedArray.getBoolean(R.styleable.CameraView_cameraPictureSizeBiggest, false)) {
            selectors.add(SizeSelectors.biggest())
        }

        if (selectors.isNotEmpty()) {
            SizeSelectors.and(*selectors.toTypedArray())
        } else {
            SizeSelectors.biggest()
        }
    }

    val videoSizeSelector: SizeSelector by lazy {
        val selectors = mutableListOf<SizeSelector>()

        if (typedArray.hasValue(R.styleable.CameraView_cameraVideoSizeMinWidth)) {
            selectors.add(SizeSelectors.minWidth(typedArray.getInteger(R.styleable.CameraView_cameraVideoSizeMinWidth, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraVideoSizeMaxWidth)) {
            selectors.add(SizeSelectors.maxWidth(typedArray.getInteger(R.styleable.CameraView_cameraVideoSizeMaxWidth, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraVideoSizeMinHeight)) {
            selectors.add(SizeSelectors.minHeight(typedArray.getInteger(R.styleable.CameraView_cameraVideoSizeMinHeight, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraVideoSizeMaxHeight)) {
            selectors.add(SizeSelectors.maxHeight(typedArray.getInteger(R.styleable.CameraView_cameraVideoSizeMaxHeight, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraVideoSizeMinArea)) {
            selectors.add(SizeSelectors.minArea(typedArray.getInteger(R.styleable.CameraView_cameraVideoSizeMinArea, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraVideoSizeMaxArea)) {
            selectors.add(SizeSelectors.maxArea(typedArray.getInteger(R.styleable.CameraView_cameraVideoSizeMaxArea, 0)))
        }
        if (typedArray.hasValue(R.styleable.CameraView_cameraVideoSizeAspectRatio)) {
            selectors.add(SizeSelectors.aspectRatio(AspectRatio.parse(typedArray.getString(R.styleable.CameraView_cameraVideoSizeAspectRatio) ?: ""), 0.0f))
        }
        if (typedArray.getBoolean(R.styleable.CameraView_cameraVideoSizeSmallest, false)) {
            selectors.add(SizeSelectors.smallest())
        }
        if (typedArray.getBoolean(R.styleable.CameraView_cameraVideoSizeBiggest, false)) {
            selectors.add(SizeSelectors.biggest())
        }

        if (selectors.isNotEmpty()) {
            SizeSelectors.and(*selectors.toTypedArray())
        } else {
            SizeSelectors.biggest()
        }
    }
}
