package com.tianshaokai.canvasdemo.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.R;
import com.tianshaokai.canvasdemo.utils.ScreenUtil;

/**
 * Created by Administrator on 2015/4/16.
 */
public class CustomView2 extends View {

    private Context mContext;
    private Paint mPaint;
    private Bitmap mBitmap;
    private int x, y;

    public CustomView2(Context context) {
        this(context, null);
    }

    public CustomView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initRes();
        initPaint();
    }

    private void initRes() {
        //获取图片
        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.image);
        //获取图片显示起始位置
        x = ScreenUtil.getScreenW(mContext) / 2 - mBitmap.getWidth() / 2;
        y = ScreenUtil.getScreenH(mContext) / 2 - mBitmap.getHeight() / 2;
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new float[]{
//                1.5F, 1.5F, 1.5F, 0, -1,
//                1.5F, 1.5F, 1.5F, 0, -1,
//                1.5F, 1.5F, 1.5F, 0, -1,
//                0, 0, 0, 1, 0
//        });
//        LightingColorFilter colorFilter = new LightingColorFilter(0xFFFFFF00, 0x00000000);
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(0XFFFF0000, PorterDuff.Mode.DARKEN);
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, x, y, mPaint);
    }
}