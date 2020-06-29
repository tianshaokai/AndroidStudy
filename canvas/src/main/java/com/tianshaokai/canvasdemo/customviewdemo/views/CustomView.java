package com.tianshaokai.canvasdemo.customviewdemo.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.AvoidXfermode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.R;
import com.tianshaokai.canvasdemo.customviewdemo.utils.MeasureUtil;


/**
 * 自定义View
 *link(http://blog.csdn.net/aigestudio/article/details/41316141)
 */
@SuppressWarnings("deprecation")
public class CustomView extends View {
    private Paint mPaint;// 画笔
    private Context mContext;// 上下文环境引用
    private Bitmap bitmap;// 位图
    private AvoidXfermode avoidXfermode;// AV模式

    private int x, y, w, h;// 位图绘制时左上角的起点坐标

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        // 初始化画笔
        initPaint();

        // 初始化资源
        initRes(context);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 实例化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		/*
         * 当画布中有跟0XFFFFFFFF色不一样的地方时候才“染”色
		 */
        avoidXfermode = new AvoidXfermode(0XFFFFFFFF, 255, AvoidXfermode.Mode.AVOID);
    }

    /**
     * 初始化资源
     */
    private void initRes(Context context) {
        // 获取位图
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.a);

		/*
		 * 计算位图绘制时左上角的坐标使其位于屏幕中心
		 * 屏幕坐标x轴向左偏移位图一半的宽度
		 * 屏幕坐标y轴向上偏移位图一半的高度
		 */
        x = MeasureUtil.getScreenSize((Activity) mContext)[0] / 2 - bitmap.getWidth() / 2;
        y = MeasureUtil.getScreenSize((Activity) mContext)[1] / 2 - bitmap.getHeight() / 2;
        w = MeasureUtil.getScreenSize((Activity) mContext)[0] / 2 + bitmap.getWidth() / 2;
        h = MeasureUtil.getScreenSize((Activity) mContext)[1] / 2 + bitmap.getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 先绘制位图
        canvas.drawBitmap(bitmap, x, y, mPaint);

        // “染”什么色是由我们自己决定的
        mPaint.setARGB(255, 211, 53, 243);

        // 设置AV模式
        mPaint.setXfermode(avoidXfermode);

        // 画一个位图大小一样的矩形
        canvas.drawRect(x, y, w, h, mPaint);
    }
}
