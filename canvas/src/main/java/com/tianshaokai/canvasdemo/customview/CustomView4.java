package com.tianshaokai.canvasdemo.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelXorXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.utils.ScreenUtil;


/**
 * Created by Administrator on 2015/4/17.
 */
public class CustomView4 extends View {

    private Paint mPaint;
    private Context mContext;
    private int x,y,w,h;

    public CustomView4(Context context) {
        this(context,null);
    }

    public CustomView4(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initDimen();
        initPaint();
    }

    private void initDimen() {
        x = ScreenUtil.getScreenW(mContext)/2 - 100;
        y = ScreenUtil.getScreenH(mContext)/2 - 100;
        w = ScreenUtil.getScreenW(mContext)/2 + 100;
        h = ScreenUtil.getScreenH(mContext)/2 + 100;
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.argb(255,255,128,114));
        mPaint.setStrokeWidth(10);
        PixelXorXfermode pixelXorXfermode = new PixelXorXfermode(0XFFFF0000);
        mPaint.setXfermode(pixelXorXfermode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(x,y,w,h,mPaint);
    }
}
