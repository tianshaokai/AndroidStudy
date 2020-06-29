package com.tianshaokai.canvasdemo.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.utils.ScreenUtil;


/**
 * Created by Administrator on 2015/4/15.
 */
public class CustomView1 extends View {

    private Paint mPaint;

    private Context mContext;

    public CustomView1(Context context) {
        this(context, null);
    }

    public CustomView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPaint();
    }

    private void initPaint() {
        //初始化Paint，并且设置消除锯齿。
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置画笔样式为描边
        mPaint.setStyle(Paint.Style.FILL);
        //设置描边的粗细，单位：像素px 注意：当setStrokeWidth(0)的时候描边宽度并不为0而是只占一个像素
        mPaint.setStrokeWidth(20);
        //设置画笔颜色为自定义颜色
        mPaint.setColor(Color.argb(255, 255, 128, 102));
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new float[]{
                0.5F, 0, 0, 0, 0,
                0, 0.5F, 0, 0, 0,
                0, 0, 0.5F, 0, 0,
                0, 0, 0, 1, 0
        });
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画一个圆形，取屏幕中心点为圆心
        canvas.drawCircle(ScreenUtil.getScreenW(mContext) / 2,
                ScreenUtil.getScreenH(mContext) / 2, 100, mPaint);
    }
}














