package com.tianshaokai.study;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tianshaokai.camera.activity.TakePhotoCamera1Activity;
import com.tianshaokai.study.adapter.MyAdapter;
import com.tianshaokai.study.entity.MyFunction;
import com.tianshaokai.study.record.AudioRecordActivity;
import com.tianshaokai.study.record.VideoRecordActivity;

import java.util.ArrayList;
import java.util.Arrays;
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

        List<MyFunction> list = Arrays.asList(
                new MyFunction("文件列表", FileListActivity.class),
                new MyFunction("屏幕密度", ScreenActivity.class),
                new MyFunction("录音", AudioRecordActivity.class),
                new MyFunction("录像", VideoRecordActivity.class),
                new MyFunction("本地存储", CacheActivity.class),
                new MyFunction("应用列表", AppListActivity.class),
                new MyFunction("网络列表", NetworkListActivity.class),
                new MyFunction("系统属性", SystemPropertyActivity.class),
                new MyFunction("画图", SignatureActivity.class),
                new MyFunction("拍照", TakePhotoCamera1Activity.class),
                new MyFunction("PAG", PAGActivity.class));
        myFunctionList.addAll(list);

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
