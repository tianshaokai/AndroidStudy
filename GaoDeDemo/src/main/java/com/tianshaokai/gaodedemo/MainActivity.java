package com.tianshaokai.gaodedemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.maps.MapView;

public class MainActivity extends AppCompatActivity {

    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        map.onCreate(savedInstanceState);
    }
}
