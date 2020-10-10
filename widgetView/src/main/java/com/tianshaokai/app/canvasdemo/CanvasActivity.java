package com.tianshaokai.app.canvasdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.tianshaokai.app.R;
import com.tianshaokai.app.canvasdemo.myview.MyView;


public class CanvasActivity extends Activity {

    private MyView myView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        myView = (MyView)findViewById(R.id.myView);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android);
        myView.setBitmap(bitmap);

        Intent intent = getIntent();
        if(intent != null){
            MyView.DrawMode drawMode = MyView.DrawMode.valueOf(intent.getIntExtra("drawMode", 0));
            myView.setDrawMode(drawMode);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myView != null){
            myView.destroy();
            myView = null;
        }
    }
}
