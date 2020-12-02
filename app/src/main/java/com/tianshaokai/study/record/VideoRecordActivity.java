package com.tianshaokai.study.record;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tianshaokai.study.R;

import java.io.File;

public class VideoRecordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VideoRecordActivity";
//    private DevicesInfoTextView tv_device;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    MediaRecorder mRecorder;
    boolean isRecording = false;
    boolean isPlaying = false;
    private File saveDir;
    private Camera mCamera;
    private File myRecAudioFile;
    private LinearLayout cameracommunication, testbtn;
    private Button returnCameraBtn, failCameraBtn, passCameraBtn,
            btn_start_camera, btn_start_camera_one, btn_start_camera_two, btn_start_camera_three, btn_start_camera_four;
    TextView tvRecordState, tvRecordStart, tvRecordStop;
    private MediaPlayer mMediaPlayer;
//    private VUMeter2 mVUMeter;

    int mState;
    public static final int IDLE_STATE = 0;
    public static final int RECORDING_STATE = 1;

    /**录制时间 5秒*/
    public static final long RECORD_TIME = 1000 * 5;

    private CountDownTimer countDownTimer = new CountDownTimer(RECORD_TIME, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            String value = String.valueOf((int) (millisUntilFinished / 1000));
            tvRecordState.setText(value);
//            mVUMeter.invalidate();
        }

        @Override
        public void onFinish() {
            tvRecordState.setText("录制结束");
            setState(IDLE_STATE);
            stopRecordVideo();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        registerFeature(this);
        setContentView(R.layout.activity_video_record);
        initView();
    }


    public void initView() {
//        tv_device = findViewById(R.id.tv_device);
//        mVUMeter = findViewById(R.id.uvMeter);
        mSurfaceView = findViewById(R.id.camera_preview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

//        btn_start_camera = findViewById(R.id.btn_start_camera);
//        btn_start_camera_one = findViewById(R.id.btn_start_camera_one);
//        btn_start_camera_two = findViewById(R.id.btn_start_camera_two);
//        btn_start_camera_three = findViewById(R.id.btn_start_camera_three);
//        btn_start_camera_four = findViewById(R.id.btn_start_camera_four);
        tvRecordState = findViewById(R.id.tvRecordState);
//        cameracommunication = findViewById(R.id.cameracommunication);

//        btn_start_camera.setOnClickListener(this);
//        btn_start_camera_one.setOnClickListener(this);
//        btn_start_camera_two.setOnClickListener(this);
//        btn_start_camera_three.setOnClickListener(this);
//        btn_start_camera_four.setOnClickListener(this);

        tvRecordState.setText(String.valueOf((RECORD_TIME / 1000)));

//        if (tv_device.isDevices1()) {
//            returnCameraBtn = findViewById(R.id.btn_return);
//            failCameraBtn = findViewById(R.id.btn_Fail);
//            passCameraBtn = findViewById(R.id.btn_Pass);
//
//            returnCameraBtn.setOnClickListener(this);
//            passCameraBtn.setOnClickListener(this);
//            failCameraBtn.setOnClickListener(this);
//        } else {
//            cameracommunication.setVisibility(View.INVISIBLE);
//        }
    }


    private void initMediaPlayer() {
        releasePlayer();
        isPlaying = true;
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.reset();//初始化

            mMediaPlayer.setDataSource(myRecAudioFile.getAbsolutePath());
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    tvRecordState.setText("回放中...");
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    releasePlayer();
                    isPlaying = false;
                    return false;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    tvRecordState.setText("回放完成");
                    isPlaying = false;
                    releasePlayer();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initRecordFile() {
        // 创建文件夹存放视频
        saveDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (!saveDir.exists()) {
            boolean mkdir = saveDir.mkdirs();
            Log.d(TAG, "文件创建成功：" + mkdir);
        }
    }

    /**
     * 开始录制Video
     */
    private void startRecordVideo() {
        stopRecordVideo();
        Log.d(TAG, "开始录制");
        initRecordFile();

        isRecording = true;
        try {

            mRecorder = new MediaRecorder();

//            int index = CameraTool.FindCameraID();
//            if (index < 0) {
//                Toast.makeText(this, "请安装摄像头", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            try {
//                mCamera = Camera.open(index);
//            } catch (Exception e) {
//                Log.e(TAG, "打开摄像头失败: " + e.toString());
//                Toast.makeText(this, "不能随意插拔摄像头，请重启机顶盒重试", Toast.LENGTH_SHORT).show();
//                return;
//            }
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(1280, 720);
            parameters.setPictureSize(1280, 720);
            mCamera.setParameters(parameters);
            mCamera.unlock();// MediaRecorder设置参数之前就调用unlock来获得camera
            mCamera.setErrorCallback(new Camera.ErrorCallback() {
                @Override
                public void onError(int error, Camera camera) {
                    isRecording = false;
                    setState(IDLE_STATE);
                    if (error == Camera.CAMERA_ERROR_SERVER_DIED) {
                        Log.e(TAG, "录制视频失败, Camera：" + error);
                    }
                }
            });


            mRecorder.setCamera(mCamera);
            myRecAudioFile = new File(saveDir, "factorytest.mp4");
            mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());       // 预览
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);     // 视频源
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);        // 录音源为麦克风
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);   // 输出格式为MP4
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(128000);
            mRecorder.setVideoSize(1280, 720);                 // 视频尺寸
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);      // 视频编码
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);       // 音频编码
            mRecorder.setVideoEncodingBitRate(150000);
            mRecorder.setOutputFile(myRecAudioFile.getAbsolutePath());       // 保存路径
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Log.e(TAG, "录制视频失败, MediaRecorder：" + what);
                    stopRecordVideo();
                    countDownTimer.cancel();
                }
            });

            mRecorder.prepare();
            mRecorder.start();

//            mVUMeter.setRecorder(this);
            countDownTimer.start();
            setState(RECORDING_STATE);
        } catch (Exception e) {
            e.printStackTrace();
            isRecording = false;
            setState(IDLE_STATE);
        }

    }

    /***
     * 停止录制Video
     */
    private void stopRecordVideo() {
        if (isRecording) {
            countDownTimer.cancel();
        }
        isRecording = false;
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "停止录像失败: " + e.toString());
        }
        try {
            if (mCamera != null) {
                mCamera.lock();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "释放Camera失败: " + e.toString());
        }
        setState(IDLE_STATE);
    }

    /**
     * 释放MediaPlayer
     */
    private void releasePlayer() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "释放MediaPlayer失败: " + e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btn_return:
//                sendFinishActivityMessage();
//                setIntentResult(RESULT_OK);
//                finish();
//                break;
//            case R.id.btn_Pass:
//                sendFinishActivityMessage();
//                setIntentResult(Item.RESULT.OK.ordinal());
//                finish();
//                break;
//            case R.id.btn_Fail:
//                sendFinishActivityMessage();
//                setIntentResult(Item.RESULT.FAIL.ordinal());
//                finish();
//                break;
//            case R.id.btn_start_camera:
//                btn_start_camera.setBackgroundResource(R.drawable.shape_green_bg);
//                if (isPlaying) {
//                    releasePlayer();
//                }
//                startRecordVideo();
//                HandlerThreadUtils.getInstance().getHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        DeviceUtils.communicationToDevice2(Constants.START_VIDEO_RECORD);
//                        DeviceUtils.communicationToDevice3(Constants.START_VIDEO_RECORD);
//                        DeviceUtils.communicationToDevice4(Constants.START_VIDEO_RECORD);
//                    }
//                });
//                break;
//            case R.id.btn_start_camera_one:
//                btn_start_camera_one.setBackgroundResource(R.drawable.shape_green_bg);
//                DeviceUtils.cutAudioOutput("1");
//                initMediaPlayer();
//                break;
//            case R.id.btn_start_camera_two:
//                btn_start_camera_two.setBackgroundResource(R.drawable.shape_green_bg);
//                DeviceUtils.cutAudioOutput("2");
//                DeviceUtils.communicationToDevice2(Constants.START_VIDEO_PLAY);
//                break;
//            case R.id.btn_start_camera_three:
//                btn_start_camera_three.setBackgroundResource(R.drawable.shape_green_bg);
//                DeviceUtils.cutAudioOutput("3");
//                DeviceUtils.communicationToDevice3(Constants.START_VIDEO_PLAY);
//                break;
//            case R.id.btn_start_camera_four:
//                btn_start_camera_four.setBackgroundResource(R.drawable.shape_green_bg);
//                DeviceUtils.cutAudioOutput("4");
//                DeviceUtils.communicationToDevice4(Constants.START_VIDEO_PLAY);
//                break;
        }
    }

    private void setIntentResult(int resultCode) {
        Intent intent = new Intent();
        setResult(resultCode, intent);
    }

    public void startRecord() {
        if (isPlaying) {
            releasePlayer();
        }
        startRecordVideo();
    }

    public void recordPlayBack() {
        initMediaPlayer();
    }

//    /***
//     * 获取在前一次调用此方法之后录音中出现的最大振幅。
//     * @return
//     */
//    public int getMaxAmplitude() {
//        if (mState == Recorder.RECORDING_STATE) {
//            return mRecorder.getMaxAmplitude();
//        }
//        return 0;
//    }

    public int state() {
        return mState;
    }

    private void setState(int recordingState) {
        this.mState = recordingState;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterFeature(this);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stopRecordVideo();
        releasePlayer();
    }

}








