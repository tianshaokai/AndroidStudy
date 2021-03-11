package com.tianshaokai.study;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tianshaokai.study.adapter.MyAdapter;
import com.tianshaokai.study.entity.MyFunction;
import com.tianshaokai.study.record.AudioRecordActivity;
import com.tianshaokai.study.record.VideoRecordActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;

    private List<MyFunction> myFunctionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);

        myFunctionList.add(new MyFunction("文件列表",   FileListActivity.class));
        myFunctionList.add(new MyFunction("屏幕密度",   ScreenActivity.class));
        myFunctionList.add(new MyFunction("录音",      AudioRecordActivity.class));
        myFunctionList.add(new MyFunction("录像",      VideoRecordActivity.class));
        myFunctionList.add(new MyFunction("本地存储",   CacheActivity.class));
        myFunctionList.add(new MyFunction("应用列表",   AppListActivity.class));
        myFunctionList.add(new MyFunction("网络列表",   NetworkListActivity.class));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new MyAdapter(myFunctionList);

        myAdapter.setOnItemClickListener((view, position, myFunction) -> {
            Intent intent = new Intent(MainActivity.this, myFunction.getClazz());
            startActivity(intent);
        });

        recyclerView.setAdapter(myAdapter);
    }
}
