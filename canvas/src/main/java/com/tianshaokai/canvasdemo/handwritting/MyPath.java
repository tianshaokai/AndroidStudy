package com.tianshaokai.canvasdemo.handwritting;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 自由曲线
 * Created by tianshaokai on 16-3-13.
 */
public class MyPath extends Action {
    Path path;
    int size;

    MyPath() {
        path = new Path();
        size = 1;
    }

    MyPath(float x, float y, int size, int color) {
        super(color);
        path = new Path();
        this.size = size;
        path.moveTo(x, y);
        path.lineTo(x, y);
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(path, paint);
    }

    public void move(float mx, float my) {
        path.lineTo(mx, my);
    }
}
