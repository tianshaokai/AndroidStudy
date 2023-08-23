package com.tianshaokai.study;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SignatureView extends View {
    private List<Path> paths = new ArrayList<>();
    private List<Float> widths = new ArrayList<>();
    private Paint paint = new Paint();
    private float lastX, lastY, lastVelocity, lastWidth;
    private long lastVelocityTime;

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if(path == null) continue;
            float width = widths.get(i);
            paint.setStrokeWidth(width);
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float pressure = event.getPressure();
        float velocity = getVelocity(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Path path = new Path();
                path.moveTo(x, y);
                paths.add(path);
                widths.add(lastWidth);
                lastX = x;
                lastY = y;
                lastVelocity = velocity;
                lastVelocityTime = 0;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Path currentPath = paths.get(paths.size() - 1);
                float dx = x - lastX;
                float dy = y - lastY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance > 0) {
                    float width = getWidth(distance, velocity, pressure);
                    currentPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
                    widths.add(width);
                    lastX = x;
                    lastY = y;
                    lastVelocityTime = System.currentTimeMillis();
                    lastVelocity = velocity;
                    lastWidth = width;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                paths.add(null); // 添加一个null对象，表示当前的路径已经绘制完成
                break;
        }
        return true;
    }

    private float getVelocity(MotionEvent event) {
        float dx = event.getX() - lastX;
        float dy = event.getY() - lastY;
        long dt = System.currentTimeMillis() - lastVelocityTime;
        return (float) Math.sqrt(dx * dx + dy * dy) / dt;
    }

//    private float getWidth(float distance, float velocity, float pressure) {
//        float speed = velocity / distance;
//        float strokeWidth = speed * pressure * 20;
//        if (strokeWidth < 5f) strokeWidth = 5f;
//        if (strokeWidth > 50f) strokeWidth = 50f;
//        return strokeWidth;
//    }

    private float getWidth(float distance, float velocity, float pressure) {
        float speed = velocity / distance;
        float acceleration = (speed - lastVelocity) / (System.currentTimeMillis() - lastVelocityTime);
        float width = (pressure + acceleration * 0.1f) * 20f;
        if (width < 1f) {
            width = 1f;
        } else if (width > 50f) {
            width = 50f;
        }
        return width;
    }

}
