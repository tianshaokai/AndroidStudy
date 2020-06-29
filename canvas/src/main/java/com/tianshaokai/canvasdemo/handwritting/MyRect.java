package com.tianshaokai.canvasdemo.handwritting;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 *  方框
 * Created by tianshaokai on 16-3-13.
 */
public class MyRect extends Action {
    float startX;
    float startY;
    float stopX;
    float stopY;
    int size;

    MyRect() {
        startX = 0;
        startY = 0;
        stopX = 0;
        stopY = 0;
    }

    MyRect(float x, float y, int size, int color) {
        super(color);
        startX = x;
        startY = y;
        stopX = x;
        stopY = y;
        this.size = size;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        canvas.drawRect(startX, startY, stopX, stopY, paint);
    }

    public void move(float mx, float my) {
        stopX = mx;
        stopY = my;
    }
}

