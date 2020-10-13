package com.tianshaokai.app.canvasdemo.largeImage;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tianshaokai.app.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class LargeImageViewActivity extends AppCompatActivity {
    private LargeImageView mLargeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image_view);

        mLargeImageView = (LargeImageView) findViewById(R.id.id_largetImageview);

//        try {
//            InputStream inputStream = getAssets().open("qm.jpg");
//            mLargeImageView.setImageInputStream(inputStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        findViewById(R.id.btnChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream inputStream = getAssets().open("world.jpg");
                    mLargeImageView.setImageInputStream(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.btnUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLargeImageView.undo();
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = mLargeImageView.toogleIsMove();
                if(flag) {
                    button.setText("写字");
                } else {
                    button.setText("拖动屏幕");
                }
            }
        });

        final Button eraser = (Button) findViewById(R.id.eraser);
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean eraserflag = mLargeImageView.toogleEraser();
                if( eraserflag) {
                    eraser.setText("笔");
                } else {
                    eraser.setText("橡皮");

                }
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      mLargeImageView.clear();
            }
        });

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LargeImageViewActivity.this, "正在保存……", Toast.LENGTH_SHORT).show();
                File file = new File(Environment.getExternalStorageDirectory(), "tmp.png");
                mLargeImageView.savePicture(file, 0.5F);
                Toast.makeText(LargeImageViewActivity.this, "保存成功……"+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
