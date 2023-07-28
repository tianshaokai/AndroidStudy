package com.tianshaokai.study;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tianshaokai.framework.util.SystemUtil;
import com.tianshaokai.study.adapter.SystemPropertyAdapter;
import com.tianshaokai.study.entity.SystemProperty;

import java.util.ArrayList;
import java.util.List;

public class SystemPropertyActivity extends AppCompatActivity {
    private RecyclerView systemPropertyRecyclerList;
    private SystemPropertyAdapter systemPropertyAdapter;

    private List<SystemProperty> systemPropertyList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_property);

        systemPropertyRecyclerList = findViewById(R.id.systemPropertyRecyclerList);

        systemPropertyRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        systemPropertyAdapter = new SystemPropertyAdapter(systemPropertyList);
        systemPropertyRecyclerList.setAdapter(systemPropertyAdapter);

        addSystemProperty();
    }

    private void addSystemProperty() {

        systemPropertyList.add(new SystemProperty("设备型号", SystemUtil.getSystemModel()));
        systemPropertyList.add(new SystemProperty("设备品牌型号", SystemUtil.getSystemBrandModel()));
        systemPropertyList.add(new SystemProperty("设备系统版本号", SystemUtil.getSystemVersion()));
        systemPropertyList.add(new SystemProperty("设备定制系统版本号", SystemUtil.getSystemModelOsVersion()));
        systemPropertyList.add(new SystemProperty("设备定制系统名称", SystemUtil.getSystemOsVersion()));

        systemPropertyAdapter.notifyDataSetChanged();
    }
}
