//package com.tianshaokai.camera.camera2.basic
//
//import android.content.Context
//import android.hardware.camera2.*
//import android.hardware.camera2.params.OutputConfiguration
//import android.hardware.camera2.params.SessionConfiguration
//import android.media.ImageReader
//import android.os.Build
//import android.os.Handler
//import android.util.Log
//import android.view.Surface
//import android.view.SurfaceHolder
//import com.tianshaokai.camera.api.CameraActions
//import com.tianshaokai.camera.api.PreviewAction
//import com.tianshaokai.camera.camera2.util.Camera2Manager
//import com.tianshaokai.camera.type.AspectRatio
//import com.tianshaokai.camera.type.CameraFacing
//import com.tianshaokai.camera.type.Size
//import com.tianshaokai.camera.type.SizeMap
//import java.util.concurrent.Semaphore
//import java.util.concurrent.TimeUnit
//
//class Camera2Engine(
//    private val context: Context,
//    private val previewAction: PreviewAction
//) : CameraActions {
//
//    private val TAG = "Camera2Engine"
//
//    private var mCameraDevice: CameraDevice? = null
//    private var mCaptureSession: CameraCaptureSession? = null
//    private var mCameraId: String = Camera2Manager.getCameraId(CameraFacing.BACK)
//    private var mSurfaceHolder: SurfaceHolder? = null
//
//    private var mAspectRatio: AspectRatio = AspectRatio.of(4, 3)
//    private val mPreviewSizes: SizeMap = SizeMap()
//    private val mPictureSizes: SizeMap = SizeMap()
//
//    private var mImageReader: ImageReader? = null
//    private val mCameraOpenCloseLock = Semaphore(1)
//
//    private val mStateCallback = object : CameraDevice.StateCallback() {
//        override fun onOpened(camera: CameraDevice) {
//            mCameraOpenCloseLock.release()
//            mCameraDevice = camera
//            createCameraPreviewSession()
//        }
//
//        override fun onDisconnected(camera: CameraDevice) {
//            mCameraOpenCloseLock.release()
//            camera.close()
//            mCameraDevice = null
//        }
//
//        override fun onError(camera: CameraDevice, error: Int) {
//            mCameraOpenCloseLock.release()
//            camera.close()
//            mCameraDevice = null
//            Log.e(TAG, "Camera device error: $error")
//        }
//    }
//
//    private fun createCameraPreviewSession() {
//        try {
//            val surface = mSurfaceHolder?.surface ?: return
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                // Android 10 (API 29) and above
//                val outputConfig = OutputConfiguration(surface)
//                val sessionConfig = SessionConfiguration(
//                    SessionConfiguration.SESSION_REGULAR,
//                    listOf(outputConfig),
//                    context.mainExecutor,
//                    object : CameraCaptureSession.StateCallback() {
//                        override fun onConfigured(session: CameraCaptureSession) {
//                        if (mCameraDevice == null) return
//
//                        mCaptureSession = session
//                        try {
//                            val previewRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
//                            previewRequestBuilder?.addTarget(surface)
//
//                            previewRequestBuilder?.let {
//                                it.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
//                                it.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
//
//                                mCaptureSession?.setRepeatingRequest(it.build(), null, null)
//                            }
//                        } catch (e: CameraAccessException) {
//                            Log.e(TAG, "Failed to start camera preview", e)
//                        }
//                    }
//
//                    override fun onConfigureFailed(session: CameraCaptureSession) {
//                        Log.e(TAG, "Failed to configure camera session")
//                    }
//                })
//
//                mCameraDevice?.createCaptureSession(sessionConfig)
//            } else {
//                // Below Android 10
//                mCameraDevice?.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
//                    override fun onConfigured(session: CameraCaptureSession) {
//                        if (mCameraDevice == null) return
//
//                        mCaptureSession = session
//                        try {
//                            val previewRequestBuilder = mCameraDevice?.createCaptureRequest(
//                                CameraDevice.TEMPLATE_PREVIEW
//                            )
//                            previewRequestBuilder?.addTarget(surface)
//
//                            previewRequestBuilder?.let {
//                                it.set(CaptureRequest.CONTROL_AF_MODE,
//                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
//                                it.set(CaptureRequest.CONTROL_AE_MODE,
//                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
//
//                                mCaptureSession?.setRepeatingRequest(
//                                    it.build(),
//                                    null,
//                                    null
//                                )
//                            }
//                        } catch (e: CameraAccessException) {
//                            Log.e(TAG, "Failed to start camera preview", e)
//                        }
//                    }
//
//                    override fun onConfigureFailed(session: CameraCaptureSession) {
//                        Log.e(TAG, "Failed to configure camera session")
//                    }
//                })
//            }
//        } catch (e: CameraAccessException) {
//            Log.e(TAG, "Failed to create camera preview session", e)
//        }
//    }
//
//    private fun openCamera(surfaceHolder: SurfaceHolder) {
//        mSurfaceHolder = surfaceHolder
//        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//        try {
//            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
//                throw RuntimeException("Time out waiting to lock camera opening.")
//            }
//
//            manager.openCamera(mCameraId, mStateCallback, null)
//        } catch (e: CameraAccessException) {
//            Log.e(TAG, "Failed to open camera", e)
//        } catch (e: SecurityException) {
//            Log.e(TAG, "Failed to open camera", e)
//        } catch (e: InterruptedException) {
//            Log.e(TAG, "Failed to open camera", e)
//        }
//    }
//
//    override fun startPreview(surfaceHolder: SurfaceHolder) {
//        openCamera(surfaceHolder)
//    }
//
//    override fun stopPreview() {
//        try {
//            mCameraOpenCloseLock.acquire()
//            mCaptureSession?.close()
//            mCaptureSession = null
//            mCameraDevice?.close()
//            mCameraDevice = null
//            mImageReader?.close()
//            mImageReader = null
//            mSurfaceHolder = null
//        } catch (e: InterruptedException) {
//            Log.e(TAG, "Failed to stop preview", e)
//        } finally {
//            mCameraOpenCloseLock.release()
//        }
//    }
//
//    override fun setCameraFacing(cameraFacing: CameraFacing): Camera2Engine {
//        this.mCameraId = Camera2Manager.getCameraId(cameraFacing)
//        return this
//    }
//}