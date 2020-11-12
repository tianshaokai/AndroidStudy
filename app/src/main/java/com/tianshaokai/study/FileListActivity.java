package com.tianshaokai.study;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tianshaokai.common.utils.FileUtil;
import com.tianshaokai.common.utils.SDUtils;

import java.io.File;

public class FileListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String path = SDUtils.getExternalFilesDir(this, "speech");

        File fileDir = new File(path);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File[] fileArray = fileDir.listFiles();
        if(fileArray == null || fileArray.length == 0) return;


        File file = fileArray[0];

        String filePath = file.getAbsolutePath();

        String content = FileUtil.getFileContent(filePath);
        Log.d("File", "文件内容: " + content);




    }
}
