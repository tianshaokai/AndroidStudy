package com.tianshaokai.study;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.libpag.PAGFile;
import org.libpag.PAGView;

public class PAGActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag1);

        PAGView pageView = findViewById(R.id.pag_view);
        pageView.setComposition(PAGFile.Load(getAssets(), "pag/fankui.pag"));
        pageView.play();
    }
}
