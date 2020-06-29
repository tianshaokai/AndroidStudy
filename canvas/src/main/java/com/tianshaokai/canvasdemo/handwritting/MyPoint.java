package com.tianshaokai.canvasdemo.handwritting;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 点
 * Created by tianshaokai on 16-3-13.
 */
public class MyPoint extends Action {
    public float x;
    public float y;

    MyPoint(float px, float py, int color) {
        super(color);
        this.x = px;
        this.y = py;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    @Override
    public void move(float mx, float my) {

    }
}
