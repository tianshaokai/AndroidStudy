package com.tianshaokai.study;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tianshaokai.common.utils.AppUtil;
import com.tianshaokai.common.utils.FileUtil;
import com.tianshaokai.common.utils.SDUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> appList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        listView = findViewById(R.id.listview);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
//            if (fileArray == null) return;
//
//            File file = fileArray[position];
//
//            String content = FileUtil.getFileContent(file.getAbsolutePath());
//            Log.d("File", "文件内容: " + content);
//
//            Intent intent = new Intent(FileListActivity.this, FileDetailActivity.class);
//            intent.putExtra("file", content);
//            startActivity(intent);
        });

        List<PackageInfo> packageInfoList = AppUtil.getInstance().getInstalledPackages(this);

        if(packageInfoList == null || packageInfoList.isEmpty()) return;

        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            appList.add((i + 1) + ". " + packageInfo.applicationInfo.loadLabel(getPackageManager()) + "" + packageInfo.applicationInfo.);
        }

        arrayAdapter.clear();
        arrayAdapter.addAll(appList);
        arrayAdapter.notifyDataSetChanged();

    }
}
