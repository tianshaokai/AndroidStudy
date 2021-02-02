package com.tianshaokai.study;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tianshaokai.common.utils.AppWifiManager;
import com.tianshaokai.common.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class NetworkListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> fileNameList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        listView = findViewById(R.id.listview);

        AppWifiManager appWifiManager = new AppWifiManager(this);

        appWifiManager.openWifi();

        List<ScanResult> scanResultList = appWifiManager.startScan();

        LogUtil.d("wifi 列表：" + scanResultList.size());
        for (int i = 0; i < scanResultList.size(); i++) {
            fileNameList.add(scanResultList.get(i).SSID + "  " + scanResultList.get(i).BSSID);
        }

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.addAll(fileNameList);
    }
}
