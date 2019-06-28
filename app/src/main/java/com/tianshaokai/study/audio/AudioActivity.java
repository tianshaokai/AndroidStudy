package com.tianshaokai.study.audio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tianshaokai.common.manager.AudioRecordManager;
import com.tianshaokai.common.utils.DateUtils;
import com.tianshaokai.common.utils.FileUtils;
import com.tianshaokai.study.R;

import java.io.File;

public class AudioActivity extends AppCompatActivity {
    private static final String TAG = "AudioActivity";
    private Button button1, button2;

    private String[] permissions = new String[] {
            Manifest.permission.RECORD_AUDIO//音频
    };

    private static final int REQUEST_PERMISSIONS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AudioActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AudioActivity.this, Manifest.permission.RECORD_AUDIO)) {
                        Toast.makeText(AudioActivity.this, "用户曾拒绝权限", Toast.LENGTH_SHORT).show();
                    } else {
                        ActivityCompat.requestPermissions(AudioActivity.this, permissions, REQUEST_PERMISSIONS);
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
    }

    private void startRecord() {
        String filePath = FileUtils.getPackageAudioPath(this);
        String audioPath = filePath + File.separator + DateUtils.getStringDate(DateUtils.Format_yMd_Hms) + ".pcm";
        AudioRecordManager audioRecordManager = AudioRecordManager.getInstance();
        audioRecordManager.setRecordPath(audioPath);
        audioRecordManager.startRecord();
    }

    private void stopRecord() {
        AudioRecordManager.getInstance().startRecord();
    }
}
