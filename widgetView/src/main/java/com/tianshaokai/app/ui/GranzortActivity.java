package com.tianshaokai.app.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tianshaokai.app.view.GranzortView;

/**
 * 能量召唤使者
 */
public class GranzortActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GranzortView(this));
    }
}
