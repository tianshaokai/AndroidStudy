package com.tianshaokai.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tianshaokai.app.canvasdemo.CanvasActivity;
import com.tianshaokai.app.canvasdemo.WaveViewActivity;
import com.tianshaokai.app.canvasdemo.customviewdemo.EraserViewActivity;
import com.tianshaokai.app.canvasdemo.customviewdemo.PolylineViewActivity;
import com.tianshaokai.app.canvasdemo.handwritting.DoodleActivity;
import com.tianshaokai.app.canvasdemo.largeImage.LargeImageViewActivity;
import com.tianshaokai.app.canvasdemo.pagecurl.activities.PageCurlActivity;
import com.tianshaokai.app.draw.BigViewActivity;
import com.tianshaokai.app.ui.FlowActivity;
import com.tianshaokai.app.ui.GranzortActivity;
import com.tianshaokai.app.ui.Loading1ViewActivity;
import com.tianshaokai.app.ui.Loading2ViewActivity;
import com.tianshaokai.app.ui.MainRecyclerViewActivity;
import com.tianshaokai.app.ui.MiUiVideoActivity;
import com.tianshaokai.app.ui.PendulumActivity;
import com.tianshaokai.app.ui.SwitchBarActivity;
import com.tianshaokai.app.ui.rotate.RotateActivity;

import java.util.ArrayList;
import java.util.List;

public class WidgetListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;

    private List<MyFunction> myFunctionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_widget);

        recyclerView = findViewById(R.id.recyclerView);

        myFunctionList.add(new MyFunction("下拉Recycler", MainRecyclerViewActivity.class));
        myFunctionList.add(new MyFunction("大图显示", BigViewActivity.class));
        myFunctionList.add(new MyFunction("大图显示2", LargeImageViewActivity.class));
        myFunctionList.add(new MyFunction("图片旋转", RotateActivity.class));
        myFunctionList.add(new MyFunction("流式布局",      FlowActivity.class));
        myFunctionList.add(new MyFunction("能量召唤使者",   GranzortActivity.class));
        myFunctionList.add(new MyFunction("TAB 切换",   SwitchBarActivity.class));
        myFunctionList.add(new MyFunction("仿小米视频进度等待",   MiUiVideoActivity.class));
        myFunctionList.add(new MyFunction("小球摆动",   PendulumActivity.class));
        myFunctionList.add(new MyFunction("炫酷的加载动画",   Loading1ViewActivity.class));
        myFunctionList.add(new MyFunction("双向小球加载动画",   Loading2ViewActivity.class));
        myFunctionList.add(new MyFunction("Canvas ",   CanvasActivity.class));
        myFunctionList.add(new MyFunction("橡皮擦 ",   EraserViewActivity.class));
        myFunctionList.add(new MyFunction("水波纹动画 ",   WaveViewActivity.class));
        myFunctionList.add(new MyFunction("应用主页面 ",   PageCurlActivity.class));
        myFunctionList.add(new MyFunction("绘图 ",   DoodleActivity.class));
        myFunctionList.add(new MyFunction("折线图 ",   PolylineViewActivity.class));


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new MyAdapter(myFunctionList);

        myAdapter.setOnItemClickListener((view, position, myFunction) -> {
            Intent intent = new Intent(WidgetListActivity.this, myFunction.getClazz());
            startActivity(intent);
        });

        recyclerView.setAdapter(myAdapter);
    }
}
