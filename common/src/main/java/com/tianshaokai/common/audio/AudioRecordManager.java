package com.tianshaokai.common.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;

import com.tianshaokai.common.utils.LogUtil;
import com.tianshaokai.common.utils.executor.ExecutorServiceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecordManager {
    private static AudioRecordManager instance;

    private AudioRecord audioRecord;

    // 声音来源
    private static int audioSource = MediaRecorder.AudioSource.MIC;
    // 采样率 Hz
//    private static int sampleRate = 44100;
    private static int sampleRate = 16000;
    // 音频通道的配置 MONO 单声道
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    // 返回音频数据的格式
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int minBufferSize;

    private boolean isRecording = false;

    private String recordPath;

    private FileOutputStream fos = null;

    private OnAudioRecordListener onAudioRecordListener;

    private AudioRecordManager() {
        //AudioRecord能接受的最小的buffer大小
        minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        LogUtil.e("AudioRecord getMinBufferSize: " + minBufferSize);
        audioRecord = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, minBufferSize);
    }


    public static AudioRecordManager getInstance() {
        if (instance == null) {
            synchronized (AudioRecordManager.class) {
                if (instance == null) {
                    instance = new AudioRecordManager();
                }
            }
        }
        return instance;
    }

    public void startRecord() {
        ExecutorServiceUtils.getSingleExecutors().execute(new Runnable() {
            @Override
            public void run() {
                //开始录音
                audioRecord.startRecording();
                isRecording = true;
                byte[] buffer = new byte[minBufferSize];
                try {
                    if (!TextUtils.isEmpty(recordPath)) {
                        File audioFile = new File(recordPath);
                        fos = new FileOutputStream(audioFile);
                    }
                    while (isRecording) {
                        int len = audioRecord.read(buffer, 0, buffer.length);
                        if (onAudioRecordListener != null) {
                            onAudioRecordListener.onVoiceRecord(buffer, len);
                        }
                        if (fos != null) {
                            fos.write(buffer, 0, len);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    audioRecord.stop();
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    public void stopRecord() {
        isRecording = false;
    }

    public AudioRecordManager setRecordPath(String recordPath) {
        this.recordPath = recordPath;
        return this;
    }

    public void setOnAudioRecordListener(OnAudioRecordListener onAudioRecordListener) {
        this.onAudioRecordListener = onAudioRecordListener;
    }

    public interface OnAudioRecordListener {
        void onVoiceRecord(byte[] data, int size);
    }
}
