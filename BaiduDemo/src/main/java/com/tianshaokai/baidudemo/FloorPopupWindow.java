package com.tianshaokai.baidudemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class FloorPopupWindow  {

    private CustomPopupWindow popupWindow;

    private ListView listview;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> list = new ArrayList<>();

    public void showAsDropDown(View view, List<String> id) {

        list.clear();

        for (int i = id.size() - 1; i >= 0 ; i --) {
            list.add(id.get(i));
        }

        if (popupWindow == null) {
            popupWindow = new CustomPopupWindow.Builder()
                    .setContext(view.getContext())
                    .setContentView(R.layout.floor_list_popup)
                    .setWidth(LinearLayout.LayoutParams.WRAP_CONTENT) //设置宽度，由于我已经在布局写好，这里就用 wrap_content就好了
                    .setHeight(LinearLayout.LayoutParams.WRAP_CONTENT) //设置高度
                    .setFocus(true)
                    .setOutSideCancel(true)
                    .builder();

            listview = (ListView) popupWindow.findViewById(R.id.listview);
            arrayAdapter = new ArrayAdapter(view.getContext(), android.R.layout.simple_list_item_1, list);
            listview.setAdapter(arrayAdapter);
        }
        popupWindow.showUp(view);
        arrayAdapter.notifyDataSetChanged();

    }

}


