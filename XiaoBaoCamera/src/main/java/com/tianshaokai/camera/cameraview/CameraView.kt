package com.tianshaokai.camera.cameraview

import android.content.Context
import android.gesture.Gesture
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.tianshaokai.camera.cameraview.controls.Audio
import com.tianshaokai.camera.cameraview.controls.Engine
import com.tianshaokai.camera.cameraview.controls.Preview
import com.tianshaokai.camera.cameraview.engine.CameraEngine
import com.tianshaokai.camera.cameraview.engine.offset.Reference
import com.tianshaokai.camera.cameraview.engine.orchestrator.CameraState
import com.tianshaokai.camera.cameraview.gesture.GestureAction
import com.tianshaokai.camera.cameraview.preview.CameraPreview
import com.tianshaokai.camera.cameraview.size.Size
import com.tianshaokai.camera.cameraview.size.SizeSelectors
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor

class CameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), LifecycleObserver {

    companion object {
        private const val DEFAULT_AUTOFOCUS_RESET_DELAY_MILLIS = 3000L
        private const val DEFAULT_FRAME_PROCESSING_EXECUTORS = 1
        private const val DEFAULT_FRAME_PROCESSING_POOL_SIZE = 2
        private const val DEFAULT_PICTURE_METERING = true
        private const val DEFAULT_PICTURE_SNAPSHOT_METERING = false
        private const val DEFAULT_PLAY_SOUNDS = true
        private const val DEFAULT_REQUEST_PERMISSIONS = true
        private const val DEFAULT_USE_DEVICE_ORIENTATION = true
        private const val PERMISSION_REQUEST_CODE = 16
        private const val TAG = "CameraView"
//        private val LOG = CameraLogger.create(TAG)
    }

    private var camera1Engine: Camera1Engine? = null
    private var iDrawFrame: IDrawFrame? = null
    private var mAutoFocusMarker: AutoFocusMarker? = null
    private var mCameraCallbacks: CameraCallbacks? = null
    private var mCameraEngine: CameraEngine? = null
    private var mCameraPreview: CameraPreview? = null
    private var mEngine: Engine? = null
    private var mExperimental = false
    private var mFrameProcessingExecutor: Executor? = null
    private var mFrameProcessingExecutors = DEFAULT_FRAME_PROCESSING_EXECUTORS
    private val mFrameProcessors = CopyOnWriteArrayList<FrameProcessor>()
    private val mGestureMap = HashMap<Gesture, GestureAction>(4)
    private var mGridLinesLayout: GridLinesLayout? = null
    private var mInEditor = false
    private var mKeepScreenOn = false
    private var mLastPreviewStreamSize: Size? = null
    private var mLifecycle: Lifecycle? = null
    private val mListeners = CopyOnWriteArrayList<CameraListener>()
    private var mMarkerLayout: MarkerLayout? = null
    private var mOrientationHelper: OrientationHelper? = null
    private var mOverlayLayout: OverlayLayout? = null
    private var mPendingFilter: Filter? = null
    private var mPinchGestureFinder: PinchGestureFinder? = null
    private var mPlaySounds = DEFAULT_PLAY_SOUNDS
    private var mPreview: Preview? = null
    private var mRequestPermissions = DEFAULT_REQUEST_PERMISSIONS
    private var mScrollGestureFinder: ScrollGestureFinder? = null
    private var mSound: MediaActionSound? = null
    private var mTapGestureFinder: TapGestureFinder? = null
    private var mUiHandler: Handler? = null
    private var mUseDeviceOrientation = DEFAULT_USE_DEVICE_ORIENTATION

    init {
        initialize(context, attrs)
    }

    private fun initialize(context: Context, attrs: AttributeSet?) {
        mInEditor = isInEditMode
        if (mInEditor) return

        setWillNotDraw(false)
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CameraView, 0, 0)
        val controlParser = ControlParser(context, typedArray)

        mExperimental = typedArray.getBoolean(R.styleable.CameraView_cameraExperimental, false)
        mRequestPermissions = typedArray.getBoolean(R.styleable.CameraView_cameraRequestPermissions, true)
        mPreview = controlParser.getPreview()
        mEngine = controlParser.getEngine()

        mCameraCallbacks = CameraCallbacks()
        mUiHandler = Handler(Looper.getMainLooper())
        mPinchGestureFinder = PinchGestureFinder(mCameraCallbacks!!)
        mTapGestureFinder = TapGestureFinder(mCameraCallbacks!!)
        mScrollGestureFinder = ScrollGestureFinder(mCameraCallbacks!!)
        mGridLinesLayout = GridLinesLayout(context)
        mOverlayLayout = OverlayLayout(context)
        mMarkerLayout = MarkerLayout(context)

        addView(mGridLinesLayout)
        addView(mMarkerLayout)
        addView(mOverlayLayout)

        doInstantiateEngine()
        setPlaySounds(DEFAULT_PLAY_SOUNDS)
        setUseDeviceOrientation(DEFAULT_USE_DEVICE_ORIENTATION)

        mOrientationHelper = OrientationHelper(context, mCameraCallbacks!!)
    }

    private fun doInstantiateEngine() {
        LOG.w("doInstantiateEngine:", "instantiating. engine:", mEngine)
        mCameraEngine = instantiateCameraEngine(mEngine!!, mCameraCallbacks!!)
        LOG.w("doInstantiateEngine:", "instantiated. engine:", mCameraEngine?.javaClass?.simpleName)
        mCameraEngine?.setOverlay(mOverlayLayout!!)
    }

    private fun doInstantiatePreview() {
        LOG.w("doInstantiatePreview:", "instantiating. preview:", mPreview)
        mCameraPreview = instantiatePreview(mPreview!!, context, this)
        LOG.w("doInstantiatePreview:", "instantiated. preview:", mCameraPreview?.javaClass?.simpleName)
        mCameraEngine?.setPreview(mCameraPreview!!)
        mPendingFilter?.let {
            setFilter(it)
            mPendingFilter = null
        }
    }

    protected fun instantiateCameraEngine(engine: Engine, callback: CameraEngine.Callback): CameraEngine {
        return if (mExperimental && engine == Engine.CAMERA2 && Build.VERSION.SDK_INT >= 21) {
            Camera2Engine(callback)
        } else {
            mEngine = Engine.CAMERA1
            Camera1Engine(callback)
        }
    }

    protected fun instantiatePreview(preview: Preview, context: Context, viewGroup: ViewGroup): CameraPreview {
        return when (preview) {
            Preview.SURFACE -> SurfaceCameraPreview(context, viewGroup)
            Preview.TEXTURE -> if (isHardwareAccelerated) {
                TextureCameraPreview(context, viewGroup)
            } else {
                GlCameraPreview(context, viewGroup)
            }
            else -> GlCameraPreview(context, viewGroup)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mInEditor) return

        if (mCameraPreview == null) {
            doInstantiatePreview()
        }
        mOrientationHelper?.enable()
    }

    override fun onDetachedFromWindow() {
        if (!mInEditor) {
            mOrientationHelper?.disable()
        }
        mLastPreviewStreamSize = null
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mInEditor) {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)
            )
            return
        }

        val previewStreamSize = mCameraEngine?.getPreviewStreamSize(Reference.VIEW)
        mLastPreviewStreamSize = previewStreamSize

        if (previewStreamSize == null) {
            LOG.w("onMeasure:", "surface is not ready. Calling default behavior.")
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        // Aspect ratio calculations and measurement logic here...
    }

    fun setRequestPermissions(requestPermissions: Boolean) {
        mRequestPermissions = requestPermissions
    }

    fun isOpened(): Boolean {
        return mCameraEngine?.state?.isAtLeast(CameraState.ENGINE) == true &&
                mCameraEngine?.targetState?.isAtLeast(CameraState.ENGINE) == true
    }

    private fun isClosed(): Boolean {
        return mCameraEngine?.state == CameraState.OFF && mCameraEngine?.isChangingState() == false
    }

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner?) {
        if (lifecycleOwner == null) {
            clearLifecycleObserver()
            return
        }
        clearLifecycleObserver()
        mLifecycle = lifecycleOwner.lifecycle
        mLifecycle?.addObserver(this)
    }

    private fun clearLifecycleObserver() {
        mLifecycle?.removeObserver(this)
        mLifecycle = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun open(iDrawFrame: IDrawFrame) {
        this.iDrawFrame = iDrawFrame
        if (mInEditor) return

        mCameraPreview?.onResume()
        if (checkPermissions(getAudio())) {
            mOrientationHelper?.enable()
            mCameraEngine?.angles?.setDisplayOffset(mOrientationHelper?.lastDisplayOffset ?: 0)
            mCameraEngine?.start()
        }
        setPreviewStreamSize(SizeSelectors.biggest())
    }

    private fun checkPermissions(audio: Audio): Boolean {
        // Permission checking logic here...
        return true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun close() {
        if (mInEditor) return

        mCameraEngine?.stop(false)
        mCameraPreview?.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        if (mInEditor) return

        clearCameraListeners()
        clearFrameProcessors()
        mCameraEngine?.destroy(true)
        mCameraPreview?.onDestroy()
    }


    fun setExperimental(experimental: Boolean) {
        mExperimental = experimental
    }

    fun set(control: Control) {
        when (control) {
            is Audio -> setAudio(control)
            is Facing -> setFacing(control)
            is Flash -> setFlash(control)
            is Grid -> setGrid(control)
            is Hdr -> setHdr(control)
            is Mode -> setMode(control)
            is WhiteBalance -> setWhiteBalance(control)
            is VideoCodec -> setVideoCodec(control)
            is Preview -> setPreview(control)
            is Engine -> setEngine(control)
            is PictureFormat -> setPictureFormat(control)
            else -> throw IllegalArgumentException("Unknown control class: ${control::class.java}")
        }
    }

    fun <T : Control> get(cls: Class<T>): T {
        return when (cls) {
            Audio::class.java -> getAudio() as T
            Facing::class.java -> getFacing() as T
            Flash::class.java -> getFlash() as T
            Grid::class.java -> getGrid() as T
            Hdr::class.java -> getHdr() as T
            Mode::class.java -> getMode() as T
            WhiteBalance::class.java -> getWhiteBalance() as T
            VideoCodec::class.java -> getVideoCodec() as T
            Preview::class.java -> getPreview() as T
            Engine::class.java -> getEngine() as T
            PictureFormat::class.java -> getPictureFormat() as T
            else -> throw IllegalArgumentException("Unknown control class: $cls")
        }
    }

    fun setPreview(preview: Preview) {
        if (preview != mPreview) {
            mPreview = preview
            if (windowToken != null || mCameraPreview == null) {
                return
            }
            mCameraPreview?.onDestroy()
            mCameraPreview = null
        }
    }

    fun getPreview(): Preview? = mPreview

    fun setEngine(engine: Engine) {
        if (isClosed()) {
            mEngine = engine
            val previousEngine = mCameraEngine
            doInstantiateEngine()
            mCameraPreview?.let { mCameraEngine?.setPreview(it) }
            previousEngine?.let {
                setFacing(it.getFacing())
                setFlash(it.getFlash())
                setMode(it.getMode())
                setWhiteBalance(it.getWhiteBalance())
                setHdr(it.getHdr())
                setAudio(it.getAudio())
                setAudioBitRate(it.getAudioBitRate())
                setPictureSize(it.getPictureSizeSelector())
                setPictureFormat(it.getPictureFormat())
                setVideoSize(it.getVideoSizeSelector())
                setVideoCodec(it.getVideoCodec())
                setVideoMaxSize(it.getVideoMaxSize())
                setVideoMaxDuration(it.getVideoMaxDuration())
                setVideoBitRate(it.getVideoBitRate())
                setAutoFocusResetDelay(it.getAutoFocusResetDelay())
                setPreviewFrameRate(it.getPreviewFrameRate())
                setPreviewFrameRateExact(it.getPreviewFrameRateExact())
                setSnapshotMaxWidth(it.getSnapshotMaxWidth())
                setSnapshotMaxHeight(it.getSnapshotMaxHeight())
                setFrameProcessingMaxWidth(it.getFrameProcessingMaxWidth())
                setFrameProcessingMaxHeight(it.getFrameProcessingMaxHeight())
                setFrameProcessingFormat(0)
                setFrameProcessingPoolSize(it.getFrameProcessingPoolSize())
                mCameraEngine?.setHasFrameProcessors(mFrameProcessors.isNotEmpty())
            }
        }
    }

    fun getEngine(): Engine? = mEngine

    fun setFilter(filter: Filter) {
        val preview = mCameraPreview
        if (preview == null) {
            mPendingFilter = filter
            return
        }
        if (preview is FilterCameraPreview) {
            preview.setFilter(filter)
        } else if (filter !is NoFilter) {
            throw RuntimeException("Filters are only supported by the GL_SURFACE preview. Current preview: $mPreview")
        }
    }

    fun getFilter(): Filter? {
        val preview = mCameraPreview
        return if (preview is FilterCameraPreview) {
            preview.getCurrentFilter()
        } else {
            mPendingFilter
        }
    }

    fun setPlaySounds(playSounds: Boolean) {
        mPlaySounds = playSounds && Build.VERSION.SDK_INT >= 16
        mCameraEngine?.setPlaySounds(playSounds)
    }

    fun getPlaySounds(): Boolean = mPlaySounds

    fun setUseDeviceOrientation(useDeviceOrientation: Boolean) {
        mUseDeviceOrientation = useDeviceOrientation
    }

    fun getUseDeviceOrientation(): Boolean = mUseDeviceOrientation

    fun addFrameProcessor(frameProcessor: FrameProcessor?) {
        frameProcessor?.let {
            mFrameProcessors.add(it)
            if (mFrameProcessors.size == 1) {
                mCameraEngine?.setHasFrameProcessors(true)
            }
        }
    }

    fun removeFrameProcessor(frameProcessor: FrameProcessor?) {
        frameProcessor?.let {
            mFrameProcessors.remove(it)
            if (mFrameProcessors.isEmpty()) {
                mCameraEngine?.setHasFrameProcessors(false)
            }
        }
    }

    fun clearFrameProcessors() {
        val hadProcessors = mFrameProcessors.isNotEmpty()
        mFrameProcessors.clear()
        if (hadProcessors) {
            mCameraEngine?.setHasFrameProcessors(false)
        }
    }

    fun setFrameProcessingExecutors(executors: Int) {
        require(executors >= 1) { "Need at least 1 executor, got $executors" }
        mFrameProcessingExecutors = executors
        val threadPoolExecutor = ThreadPoolExecutor(
            executors, executors, 4L, TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            ThreadFactory { Thread(it, "FrameExecutor #${AtomicInteger(1).getAndIncrement()}") }
        )
        threadPoolExecutor.allowCoreThreadTimeOut(true)
        mFrameProcessingExecutor = threadPoolExecutor
    }

    fun getFrameProcessingExecutors(): Int = mFrameProcessingExecutors

    // Additional methods can be added here...

    private fun isClosed(): Boolean {
        return mCameraEngine?.state == CameraState.OFF && mCameraEngine?.isChangingState() == false
    }

    private fun doInstantiateEngine() {
        // Logic for instantiating the camera engine
    }



    // 获取相机选项
    fun getCameraOptions(): CameraOptions? {
        return mCameraEngine?.getCameraOptions()
    }

    // 设置曝光补偿
    fun setExposureCorrection(value: Float) {
        val cameraOptions = getCameraOptions()
        if (cameraOptions != null) {
            val minValue = cameraOptions.getExposureCorrectionMinValue()
            val maxValue = cameraOptions.getExposureCorrectionMaxValue()
            val correctedValue = value.coerceIn(minValue, maxValue) // 使用 Kotlin 的 `coerceIn` 方法
            mCameraEngine?.setExposureCorrection(
                correctedValue,
                floatArrayOf(minValue, maxValue),
                null,
                false
            )
        }
    }

    // 获取曝光补偿值
    fun getExposureCorrection(): Float {
        return mCameraEngine?.getExposureCorrectionValue() ?: 0f
    }

    // 设置缩放
    fun setZoom(value: Float) {
        val correctedValue = value.coerceIn(0f, 1f) // 限制值在 0 到 1 之间
        mCameraEngine?.setZoom(correctedValue, null, false)
    }

    // 获取缩放值
    fun getZoom(): Float {
        return mCameraEngine?.getZoomValue() ?: 0f
    }

    // 设置网格模式
    fun setGrid(grid: Grid) {
        mGridLinesLayout?.setGridMode(grid)
    }

    // 获取网格模式
    fun getGrid(): Grid? {
        return mGridLinesLayout?.getGridMode()
    }

    // 设置网格颜色
    fun setGridColor(color: Int) {
        mGridLinesLayout?.setGridColor(color)
    }

    // 获取网格颜色
    fun getGridColor(): Int {
        return mGridLinesLayout?.getGridColor() ?: 0
    }

    // 设置 HDR 模式
    fun setHdr(hdr: Hdr) {
        mCameraEngine?.setHdr(hdr)
    }

    // 获取 HDR 模式
    fun getHdr(): Hdr? {
        return mCameraEngine?.getHdr()
    }

    // 设置地理位置（经纬度）
    fun setLocation(latitude: Double, longitude: Double) {
        val location = Location("Unknown").apply {
            time = System.currentTimeMillis()
            altitude = 0.0
            this.latitude = latitude
            this.longitude = longitude
        }
        mCameraEngine?.setLocation(location)
    }

    // 设置地理位置（Location 对象）
    fun setLocation(location: Location) {
        mCameraEngine?.setLocation(location)
    }

    // 获取地理位置
    fun getLocation(): Location? {
        return mCameraEngine?.getLocation()
    }

    // 设置白平衡
    fun setWhiteBalance(whiteBalance: WhiteBalance) {
        mCameraEngine?.setWhiteBalance(whiteBalance)
    }

    // 获取白平衡
    fun getWhiteBalance(): WhiteBalance? {
        return mCameraEngine?.getWhiteBalance()
    }

    // 设置摄像头方向
    fun setFacing(facing: Facing) {
        mCameraEngine?.setFacing(facing)
    }

    // 获取摄像头方向
    fun getFacing(): Facing? {
        return mCameraEngine?.getFacing()
    }



    // 切换摄像头方向
    fun toggleFacing(): Facing? {
        val currentFacing = mCameraEngine?.getFacing()
        when (currentFacing) {
            Facing.BACK -> setFacing(Facing.FRONT)
            Facing.FRONT -> setFacing(Facing.BACK)
        }
        return mCameraEngine?.getFacing()
    }

    // 设置闪光灯模式
    fun setFlash(flash: Flash) {
        mCameraEngine?.setFlash(flash)
    }

    // 获取闪光灯模式
    fun getFlash(): Flash? {
        return mCameraEngine?.getFlash()
    }

    // 设置音频模式
    fun setAudio(audio: Audio) {
        if (audio == getAudio() || isClosed()) {
            mCameraEngine?.setAudio(audio)
        } else if (checkPermissions(audio)) {
            mCameraEngine?.setAudio(audio)
        } else {
            close()
        }
    }

    // 获取音频模式
    fun getAudio(): Audio? {
        return mCameraEngine?.getAudio()
    }

    // 设置自动对焦标记
    fun setAutoFocusMarker(autoFocusMarker: AutoFocusMarker) {
        mAutoFocusMarker = autoFocusMarker
        mMarkerLayout?.onMarker(1, autoFocusMarker)
    }

    // 设置自动对焦重置延迟
    fun setAutoFocusResetDelay(delay: Long) {
        mCameraEngine?.setAutoFocusResetDelay(delay)
    }

    // 获取自动对焦重置延迟
    fun getAutoFocusResetDelay(): Long {
        return mCameraEngine?.getAutoFocusResetDelay() ?: 0L
    }

    // 开始自动对焦（通过坐标）
    fun startAutoFocus(x: Float, y: Float) {
        require(x in 0.0f..width.toFloat()) { "x should be >= 0 and <= getWidth()" }
        require(y in 0.0f..height.toFloat()) { "y should be >= 0 and <= getHeight()" }

        val size = Size(width, height)
        val point = PointF(x, y)
        mCameraEngine?.startAutoFocus(null, MeteringRegions.fromPoint(size, point), point)
    }

    // 开始自动对焦（通过区域）
    fun startAutoFocus(rect: RectF) {
        require(RectF(0f, 0f, width.toFloat(), height.toFloat()).contains(rect)) {
            "Region is out of view bounds! $rect"
        }

        val size = Size(width, height)
        val centerPoint = PointF(rect.centerX(), rect.centerY())
        mCameraEngine?.startAutoFocus(null, MeteringRegions.fromArea(size, rect), centerPoint)
    }

    // 设置预览流大小
    fun setPreviewStreamSize(sizeSelector: SizeSelector) {
        mCameraEngine?.setPreviewStreamSizeSelector(sizeSelector)
    }

    // 设置模式
    fun setMode(mode: Mode) {
        mCameraEngine?.setMode(mode)
    }

    // 获取模式
    fun getMode(): Mode? {
        return mCameraEngine?.getMode()
    }

    // 设置图片大小
    fun setPictureSize(sizeSelector: SizeSelector) {
        mCameraEngine?.setPictureSizeSelector(sizeSelector)
    }

    // 设置图片测光
    fun setPictureMetering(enabled: Boolean) {
        mCameraEngine?.setPictureMetering(enabled)
    }

    // 获取图片测光
    fun getPictureMetering(): Boolean {
        return mCameraEngine?.getPictureMetering() ?: false
    }

    // 设置图片快照测光
    fun setPictureSnapshotMetering(enabled: Boolean) {
        mCameraEngine?.setPictureSnapshotMetering(enabled)
    }

    // 获取图片快照测光
    fun getPictureSnapshotMetering(): Boolean {
        return mCameraEngine?.getPictureSnapshotMetering() ?: false
    }

    // 设置图片格式
    fun setPictureFormat(pictureFormat: PictureFormat) {
        mCameraEngine?.setPictureFormat(pictureFormat)
    }

    // 获取图片格式
    fun getPictureFormat(): PictureFormat? {
        return mCameraEngine?.getPictureFormat()
    }

    // 设置视频大小
    fun setVideoSize(sizeSelector: SizeSelector) {
        mCameraEngine?.setVideoSizeSelector(sizeSelector)
    }

    // 设置视频比特率
    fun setVideoBitRate(bitRate: Int) {
        mCameraEngine?.setVideoBitRate(bitRate)
    }

    // 获取视频比特率
    fun getVideoBitRate(): Int {
        return mCameraEngine?.getVideoBitRate() ?: 0
    }

    // 设置预览帧率（精确）
    fun setPreviewFrameRateExact(enabled: Boolean) {
        mCameraEngine?.setPreviewFrameRateExact(enabled)
    }

    // 获取预览帧率（精确）
    fun getPreviewFrameRateExact(): Boolean {
        return mCameraEngine?.getPreviewFrameRateExact() ?: false
    }

    // 设置预览帧率
    fun setPreviewFrameRate(frameRate: Float) {
        mCameraEngine?.setPreviewFrameRate(frameRate)
    }

    // 获取预览帧率
    fun getPreviewFrameRate(): Float {
        return mCameraEngine?.getPreviewFrameRate() ?: 0f
    }

    // 设置音频比特率
    fun setAudioBitRate(bitRate: Int) {
        mCameraEngine?.setAudioBitRate(bitRate)
    }

    // 获取音频比特率
    fun getAudioBitRate(): Int {
        return mCameraEngine?.getAudioBitRate() ?: 0
    }

    // 添加相机监听器
    fun addCameraListener(listener: CameraListener) {
        mListeners.add(listener)
    }

    // 移除相机监听器
    fun removeCameraListener(listener: CameraListener) {
        mListeners.remove(listener)
    }

    // 清除所有相机监听器
    fun clearCameraListeners() {
        mListeners.clear()
    }

    // 拍照
    fun takePicture() {
        mCameraEngine?.takePicture(PictureResult.Stub())
    }

    // 拍照（带参数）
    fun takePicture(bool: Boolean) {
        takePicture()
    }

    // 拍摄快照
    fun takePictureSnapshot() {
        mCameraEngine?.takePictureSnapshot(PictureResult.Stub())
    }

    // 拍摄视频
    fun takeVideo(file: File) {
        takeVideo(file, null)
    }

    // 拍摄视频（带文件描述符）
    fun takeVideo(fileDescriptor: FileDescriptor) {
        takeVideo(null, fileDescriptor)
    }

    // 停止视频录制
    fun stopVideo() {
        mCameraEngine?.stopVideo()
        mUiHandler.post {
            if (keepScreenOn != mKeepScreenOn) {
                keepScreenOn = mKeepScreenOn
            }
        }
    }

    // 设置视频最大时长
    fun setVideoMaxDuration(duration: Int) {
        mCameraEngine?.setVideoMaxDuration(duration)
    }

    // 获取视频最大时长
    fun getVideoMaxDuration(): Int {
        return mCameraEngine?.getVideoMaxDuration() ?: 0
    }

    // 检查是否正在录制视频
    fun isTakingVideo(): Boolean {
        return mCameraEngine?.isTakingVideo() ?: false
    }

    // 检查是否正在拍照
    fun isTakingPicture(): Boolean {
        return mCameraEngine?.isTakingPicture() ?: false
    }

}
