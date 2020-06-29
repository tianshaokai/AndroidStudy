package com.tianshaokai.app.ui;

import android.content.Intent;
import android.view.View;

import com.tianshaokai.app.R;
import com.tianshaokai.app.draw.BigViewActivity;

import butterknife.OnClick;

public class DrawActivity extends BaseActivity {

    @Override
    protected int initLayoutViewID() {
        return R.layout.activity_draw;
    }

    @OnClick({R.id.button1, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                Intent intent = new Intent(mContent, BigViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button2: {
                Intent intent = new Intent(mContent, FlowActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
