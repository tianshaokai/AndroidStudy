package com.tianshaokai.app;

import android.content.Intent;
import android.view.View;

import com.tianshaokai.app.ui.BaseActivity;
import com.tianshaokai.app.ui.DrawActivity;
import com.tianshaokai.app.ui.GranzortActivity;
import com.tianshaokai.app.ui.Loading1ViewActivity;
import com.tianshaokai.app.ui.Loading2ViewActivity;
import com.tianshaokai.app.ui.MiUiVideoActivity;
import com.tianshaokai.app.ui.PendulumActivity;
import com.tianshaokai.app.ui.SwitchBarActivity;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected int initLayoutViewID() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1: {
                Intent intent = new Intent(mContent, GranzortActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button2: {
                Intent intent = new Intent(mContent, SwitchBarActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button3: {
                Intent intent = new Intent(mContent, MiUiVideoActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button4: {
                Intent intent = new Intent(mContent, PendulumActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button5: {
                Intent intent = new Intent(mContent, Loading1ViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button6: {
                Intent intent = new Intent(mContent, Loading2ViewActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button7: {
                Intent intent = new Intent(mContent, DrawActivity.class);
                startActivity(intent);
                break;
            }
        }
    }


}
