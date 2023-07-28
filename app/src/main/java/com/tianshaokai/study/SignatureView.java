package com.tianshaokai.study;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View {
    private Path path = new Path();
    private Paint paint = new Paint();
    private float lastX, lastY, lastVelocity, lastWidth;

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
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float velocity = getVelocity(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                lastX = x;
                lastY = y;
                lastVelocity = velocity;
                lastWidth = paint.getStrokeWidth();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - lastX;
                float dy = y - lastY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance > 0) {
                    float width = getWidth(distance, velocity);
                    path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
                    paint.setStrokeWidth(width);
                    lastX = x;
                    lastY = y;
                    lastVelocity = velocity;
                    lastWidth = width;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                path.lineTo(x, y);
                invalidate();
                break;
        }
        return true;
    }

    private float getVelocity(MotionEvent event) {
        int historySize = event.getHistorySize();
        if (historySize < 1) {
            return 0;
        }
        float dx = event.getX() - event.getHistoricalX(0);
        float dy = event.getY() - event.getHistoricalY(0);
        float dt = event.getEventTime() - event.getHistoricalEventTime(0);
        return (float) Math.sqrt(dx * dx + dy * dy) / dt;
    }

    private float getWidth(float distance, float velocity) {
        float acceleration = (velocity - lastVelocity) / distance;
        float width = lastWidth + acceleration * distance;
        return Math.max(1f, width);
    }
}


