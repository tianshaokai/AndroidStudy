package com.tianshaokai.study.record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tianshaokai.common.audio.AudioRecordManager;
import com.tianshaokai.common.audio.AudioTrackManager;
import com.tianshaokai.common.utils.DateUtil;
import com.tianshaokai.common.utils.FileUtil;
import com.tianshaokai.study.R;

import java.io.File;

public class AudioRecordActivity extends AppCompatActivity {
    private static final String TAG = "AudioActivity";
    private Button button1, button2, button3, button4;

    private String[] permissions = new String[] {
            Manifest.permission.RECORD_AUDIO//音频
    };

    private String audioPath;

    private static final int REQUEST_PERMISSIONS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AudioRecordActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AudioRecordActivity.this, Manifest.permission.RECORD_AUDIO)) {
                        Toast.makeText(AudioRecordActivity.this, "用户曾拒绝权限", Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(AudioRecordActivity.this, permissions, REQUEST_PERMISSIONS);
                    }
                } else {
                    startRecord();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioTrackManager.getInstance().setDataSource(audioPath).play();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioTrackManager.getInstance().stop();
            }
        });
    }

    private void startRecord() {
        String filePath = FileUtil.getPackageAudioPath(this);
        audioPath = filePath + File.separator + DateUtil.getTimeStamp() + ".pcm";
        AudioRecordManager.getInstance().setRecordPath(audioPath).startRecord();
    }

    private void stopRecord() {
        AudioRecordManager.getInstance().stopRecord();
    }
}
