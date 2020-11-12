package com.tianshaokai.study;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tianshaokai.common.utils.FileUtil;
import com.tianshaokai.common.utils.SDUtils;

import java.io.File;
import java.util.ArrayList;

public class FileListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> fileNameList = new ArrayList<>();

    private  File[] fileArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        listView = findViewById(R.id.listview);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fileArray == null) return;

                File file = fileArray[position];

                String content = FileUtil.getFileContent(file.getAbsolutePath());
                Log.d("File", "文件内容: " + content);
            }
        });

        fileArray = getFilesArray();

        if(fileArray == null || fileArray.length == 0) return;

        for (int i = 0; i < fileArray.length; i++) {
            File file = fileArray[i];
            fileNameList.add((i + 1) + ". " + file.getName());
        }

        arrayAdapter.clear();
        arrayAdapter.addAll(fileNameList);
        arrayAdapter.notifyDataSetChanged();
    }

    private File[] getFilesArray() {
        String path = SDUtils.getExternalFilesDir(this, "speech");

        File fileDir = new File(path);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        return fileDir.listFiles();
    }
}
