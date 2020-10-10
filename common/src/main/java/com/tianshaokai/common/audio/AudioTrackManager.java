package com.tianshaokai.common.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.text.TextUtils;

import com.tianshaokai.common.utils.executor.ExecutorServiceUtils;
import com.tianshaokai.common.utils.LogUtil;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioTrackManager {

    private static final String TAG = "AudioTrackManager";

    private static AudioTrackManager instance;

    private AudioTrack audioTrack;

    // 采样率 Hz
//    private static int sampleRate = 44100;
    private static int sampleRate = 16000;

    // 音频通道的配置 MONO 单声道
    private static int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    // 返回音频数据的格式
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private int bufferSize;

    private String audioPath;

    private boolean isPlaying = false;

    private AudioTrackManager() {
        bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, channelConfig, audioFormat, bufferSize, AudioTrack.MODE_STREAM);
    }

    public static AudioTrackManager getInstance() {
        if (instance == null) {
            synchronized (AudioTrackManager.class) {
                if (instance == null) {
                    instance = new AudioTrackManager();
                }
            }
        }
        return instance;
    }

    public AudioTrackManager setDataSource(String audioPath) {
        this.audioPath = audioPath;
        return this;
    }

    public void play() {
        if (TextUtils.isEmpty(audioPath)) {
            LogUtil.e(TAG, "audioPath is null");
            return;
        }
        ExecutorServiceUtils.getSingleExecutors().execute(new Runnable() {
            @Override
            public void run() {
                if (isPlaying) {
                    stop();
                }
                audioTrack.play();
                isPlaying = true;
                byte[] buffer = new byte[bufferSize];
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(audioPath);
                    int len = 0;
                    while ((len = fis.read(buffer)) != -1 && isPlaying) {
                        audioTrack.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public void stop() {
        isPlaying = false;
        audioTrack.stop();
    }

}
