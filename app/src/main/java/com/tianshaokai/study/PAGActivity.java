package com.tianshaokai.study;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.libpag.PAGFile;
import org.libpag.PAGImageView;
import org.libpag.PAGView;

public class PAGActivity extends AppCompatActivity {

    private PAGView pageView;
    private PAGImageView pageImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag1);

        pageView = findViewById(R.id.pag_view);
        pageView.setComposition(PAGFile.Load(getAssets(), "pag/fankui.pag"));
        pageView.play();


        pageImageView = findViewById(R.id.pag_imageview);
        pageImageView.setComposition(PAGFile.Load(getAssets(), "pag/pag_blink.pag"));
        pageImageView.play();
    }
}
