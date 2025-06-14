package com.tianshaokai.study;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SignatureView extends View {
    private List<Path> paths = new ArrayList<>();
    private List<Float> widths = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();
    private Paint paint = new Paint();
    
    // 用于贝塞尔曲线的点
    private PointF mLastPoint = new PointF();
    private PointF mControlPoint1 = new PointF();
    private PointF mControlPoint2 = new PointF();
    private PointF mCurrentPoint = new PointF();
    
    private float lastVelocity, lastWidth;
    private long lastVelocityTime;
    
    // 默认画笔颜色和粗细
    private int currentColor = Color.BLACK;
    private float currentStrokeWidth = 5f;
    private boolean useDynamicWidth = true; // 是否使用动态宽度

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        paint.setColor(currentColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(currentStrokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setSubpixelText(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 启用硬件加速
        setLayerType(LAYER_TYPE_HARDWARE, null);
        
        for (int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            if(path == null) continue;
            float width = widths.get(i);
            int color = colors.get(i);
            paint.setStrokeWidth(width);
            paint.setColor(color);
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
                widths.add(useDynamicWidth ? getWidth(0, velocity, pressure) : currentStrokeWidth);
                colors.add(currentColor);
                
                // 初始化贝塞尔曲线的点
                mLastPoint.set(x, y);
                mCurrentPoint.set(x, y);
                mControlPoint1.set(x, y);
                mControlPoint2.set(x, y);
                
                lastVelocity = velocity;
                lastVelocityTime = 0;
                invalidate();
                break;
                
            case MotionEvent.ACTION_MOVE:
                Path currentPath = paths.get(paths.size() - 1);
                float dx = x - mLastPoint.x;
                float dy = y - mLastPoint.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                
                if (distance > 0) {
                    // 更新控制点，使用更平滑的贝塞尔曲线
                    mControlPoint1.set(mLastPoint.x + dx * 0.3f, mLastPoint.y + dy * 0.3f);
                    mControlPoint2.set(x - dx * 0.3f, y - dy * 0.3f);
                    
                    // 使用三次贝塞尔曲线
                    currentPath.cubicTo(
                        mControlPoint1.x, mControlPoint1.y,
                        mControlPoint2.x, mControlPoint2.y,
                        x, y
                    );
                    
                    float width = useDynamicWidth ? getWidth(distance, velocity, pressure) : currentStrokeWidth;
                    widths.add(width);
                    colors.add(currentColor);
                    
                    // 更新点
                    mLastPoint.set(x, y);
                    mCurrentPoint.set(x, y);
                    
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
        float dx = event.getX() - mLastPoint.x;
        float dy = event.getY() - mLastPoint.y;
        long dt = System.currentTimeMillis() - lastVelocityTime;
        if (dt == 0) return 0;
        return (float) Math.sqrt(dx * dx + dy * dy) / dt;
    }

    private float getWidth(float distance, float velocity, float pressure) {
        float speed = velocity / (distance + 0.1f); // 避免除以零
        float acceleration = (speed - lastVelocity) / (System.currentTimeMillis() - lastVelocityTime + 1);
        float width = (pressure + acceleration * 0.1f) * 15f; // 减小系数使线条更平滑
        if (width < 2f) { // 增加最小宽度
            width = 2f;
        } else if (width > 30f) { // 减小最大宽度
            width = 30f;
        }
        return width;
    }

    /**
     * 设置画笔颜色
     */
    public void setColor(int color) {
        this.currentColor = color;
        paint.setColor(color);
        invalidate();
    }

    /**
     * 设置画笔粗细
     */
    public void setStrokeWidth(float width) {
        this.currentStrokeWidth = width;
        paint.setStrokeWidth(width);
        invalidate();
    }

    /**
     * 设置是否使用动态宽度
     */
    public void setUseDynamicWidth(boolean useDynamicWidth) {
        this.useDynamicWidth = useDynamicWidth;
    }

    /**
     * 撤回上一步
     */
    public void undo() {
        if (paths.isEmpty()) return;
        
        // 找到上一个非null的路径
        int index = paths.size() - 1;
        while (index >= 0 && paths.get(index) == null) {
            index--;
        }
        
        // 删除路径及其对应的宽度和颜色
        if (index >= 0) {
            paths.remove(index);
            widths.remove(index);
            colors.remove(index);
            invalidate();
        }
    }

    /**
     * 清空画布
     */
    public void clear() {
        paths.clear();
        widths.clear();
        colors.clear();
        invalidate();
    }
}
