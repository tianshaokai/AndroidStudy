package com.tianshaokai.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tianshaokai.app.view.Pendulum;

/***
 * 时钟 摆动的view
 */
public class PendulumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Pendulum(this));
    }
}
