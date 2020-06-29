package com.tianshaokai.canvasdemo.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义的view，需要覆盖onDraw()方法绘制控件，覆盖onTouchEvent()接收触摸消息
 */
public class CustomView extends View {

    private static final int WIDTH = 200;

    private Rect rect = new Rect(0, 0, WIDTH, WIDTH);//绘制矩形的区域
    private int deltaX,deltaY;//点击位置和图形边界的偏移量
    private static Paint paint = new Paint();//画笔

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.RED);//填充红色
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(rect, paint);//画矩形

    }

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!rect.contains(x, y)) {
                    return false;//没有在矩形上点击，不处理触摸消息
                }
                deltaX = x - rect.left;
                deltaY = y - rect.top;
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                Rect old = new Rect(rect);
                //更新矩形的位置
                rect.left = x - deltaX;
                rect.top = y - deltaY;
                rect.right = rect.left + WIDTH;
                rect.bottom = rect.top + WIDTH;
                old.union(rect);//要刷新的区域，求新矩形区域与旧矩形区域的并集
                invalidate(old);//出于效率考虑，设定脏区域，只进行局部刷新，不是刷新整个view
                break;
        }
        return true;//处理了触摸消息，消息不再传递
    }

}
