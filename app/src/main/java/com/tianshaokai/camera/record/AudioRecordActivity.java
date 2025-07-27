package com.tianshaokai.camera.record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tianshaokai.common.audio.AudioRecordManager;
import com.tianshaokai.common.audio.AudioTrackManager;
import com.tianshaokai.common.utils.FileUtil;
import com.tianshaokai.framework.util.DateUtils;
import com.tianshaokai.framework.util.SDUtils;
import com.tianshaokai.camera.R;
import com.tianshaokai.camera.adapter.AudioRecordListAdapter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

public class AudioRecordActivity extends AppCompatActivity {
    private static final String TAG = "AudioActivity";
    private Button button1, button2, button3, button4, button5;

    private String[] permissions = new String[] {
            Manifest.permission.RECORD_AUDIO//音频
    };

    private String audioPath;

    private static final int REQUEST_PERMISSIONS = 1000;

    private RecyclerView audioRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);

        audioRecyclerView = findViewById(R.id.audioRecyclerView);

        final AudioRecordListAdapter audioRecordListAdapter = new AudioRecordListAdapter(getAudioList());
        audioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        audioRecyclerView.setAdapter(audioRecordListAdapter);

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
                audioRecordListAdapter.addFile(audioPath);
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
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = SDUtils.getPackageAudioPath(AudioRecordActivity.this);
                FileUtil.deleteFile(filePath);
                audioRecordListAdapter.deleteFile();
            }
        });
    }

    private void startRecord() {
        String filePath = SDUtils.getPackageAudioPath(this);
        audioPath = filePath + File.separator + DateUtils.getTimeStamp() + ".pcm";
        AudioRecordManager.getInstance().setRecordPath(audioPath).startRecord();
    }

    private void stopRecord() {
        AudioRecordManager.getInstance().stopRecord();
    }

    private List<File> getAudioList() {
        String filePath = SDUtils.getPackageAudioPath(this);
        File[] fileArray = new File(filePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && !pathname.isHidden() && pathname.getName().endsWith(".pcm");
            }
        });
        return Arrays.asList(fileArray);
    }
}
