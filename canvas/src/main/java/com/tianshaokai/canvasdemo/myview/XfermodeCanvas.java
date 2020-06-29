package com.tianshaokai.canvasdemo.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.R;

/**
 * Created by tianshaokai on 2016/3/10.
 */
public class XfermodeCanvas extends View {

    private Paint mPaint;

    public XfermodeCanvas(Context context) {
        super(context);
        init();
    }

    public XfermodeCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);//设画布绿色
        canvas.translate(20, 20);//移动画布
        // 原始图片
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.mipmap.meinv);
        // 图片的遮罩
        Bitmap mask = Bitmap.createBitmap(300, 300, src.getConfig());
        Canvas cc = new Canvas(mask);
        cc.drawCircle(150, 150, 150, mPaint);
        /*
        * 离屏缓存
        * Layer层的宽和高要设定好，不然会出现有些部位不再层里面，你的操作是不对这些部位起作用的
        */
        int sc = canvas.saveLayer(0, 0, 300, 300, null, Canvas.ALL_SAVE_FLAG);
        // 先绘制dis目标图
        canvas.drawBitmap(src, 0, 0, mPaint);
        // 设置混合模式 （只在源图像和目标图像相交的地方绘制目标图像）
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // 再绘制src源图
        canvas.drawBitmap(mask, 0, 0, mPaint);
        // 还原混合模式
        mPaint.setXfermode(null);
        // 还原画布
        canvas.restoreToCount(sc);
    }
}
