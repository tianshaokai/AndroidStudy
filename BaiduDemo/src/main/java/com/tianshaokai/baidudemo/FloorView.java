package com.tianshaokai.baidudemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baidu.mapapi.map.MapBaseIndoorMapInfo;

import java.util.ArrayList;
import java.util.List;

public class FloorView extends LinearLayout {
    private ListView listview;
    private ArrayList<String> floorList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private CallBack callBack;
    private MapBaseIndoorMapInfo mapBaseIndoorMapInfo;

    public FloorView(Context context) {
        super(context);
    }

    public FloorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        listview = findViewById(R.id.listview);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (callBack != null && mapBaseIndoorMapInfo != null) {
                    callBack.onClick(floorList.get(position), mapBaseIndoorMapInfo.getID());
                }
            }
        });
    }

    public void showFloor(MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
        floorList.clear();
        this.mapBaseIndoorMapInfo = mapBaseIndoorMapInfo;
        List<String> floors = mapBaseIndoorMapInfo.getFloors();
        if(floors == null || floors.isEmpty()) return;
        for (int i = floors.size() - 1; i >= 0 ; i --) {
            floorList.add(floors.get(i));
        }
        arrayAdapter.clear();
        arrayAdapter.addAll(floorList);
        arrayAdapter.notifyDataSetChanged();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void onClick(String strFloor, String floorID);
    }


}


