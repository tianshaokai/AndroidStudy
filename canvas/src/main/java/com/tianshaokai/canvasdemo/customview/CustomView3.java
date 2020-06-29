package com.tianshaokai.canvasdemo.customview;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelXorXfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.R;
import com.tianshaokai.canvasdemo.utils.ScreenUtil;

/**
 * Created by Administrator on 2015/4/16.
 */
public class CustomView3 extends View {

    private Paint mPaint;
    private Bitmap mBitmap;
    private Context mContext;
    private int x, y, w, h;
    private PixelXorXfermode pixelXorXfermode;

    public CustomView3(Context context) {
        this(context, null);
    }

    public CustomView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRes();
        initPaint();

    }

    private void initRes() {
        //加载bitmap
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.image);
        //获取bitmap的展示起始布局
        x = ScreenUtil.getScreenW(mContext) / 2 - mBitmap.getWidth() / 2;
        y = ScreenUtil.getScreenH(mContext) / 2 - mBitmap.getHeight() / 2;
        w = ScreenUtil.getScreenW(mContext) / 2 + mBitmap.getWidth() / 2;
        h = ScreenUtil.getScreenH(mContext) / 2 + mBitmap.getHeight() / 2;
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pixelXorXfermode = new PixelXorXfermode(0XFFFF0000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //先绘制Bitmap，src
        canvas.drawBitmap(mBitmap, x, y, mPaint);
        //随便设置一个纯色测试
        mPaint.setARGB(255, 211, 53, 243);
        //设置Xfermode
        mPaint.setXfermode(pixelXorXfermode);
        //在bitmap上混排一个纯色的矩形（dst）
        canvas.drawRect(x, y, w, h, mPaint);
    }
}




