package com.tianshaokai.canvasdemo.customviewdemo.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.customviewdemo.bo.PorterDuffBO;
import com.tianshaokai.canvasdemo.customviewdemo.utils.MeasureUtil;


/**
 * 测试不同PorterDuff模式的View
 *  link(http://blog.csdn.net/aigestudio/article/details/41316141)
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PorterDuffView extends View {
    /*
     * PorterDuff模式常量
     * 可以在此更改不同的模式测试
     */
    private static final PorterDuff.Mode MODE = PorterDuff.Mode.XOR;

    private static final int RECT_SIZE_SMALL = 400;// 左右上方示例渐变正方形的尺寸大小
    private static final int RECT_SIZE_BIG = 800;// 中间测试渐变正方形的尺寸大小

    private Paint mPaint;// 画笔

    private PorterDuffBO porterDuffBO;// PorterDuffView类的业务对象
    private PorterDuffXfermode porterDuffXfermode;// 图形混合模式

    private int screenW, screenH;// 屏幕尺寸
    private int s_l, s_t;// 左上方正方形的原点坐标
    private int d_l, d_t;// 右上方正方形的原点坐标
    private int rectX, rectY;// 中间正方形的原点坐标

    public PorterDuffView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 实例化画笔并设置抗锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // 实例化业务对象
        porterDuffBO = new PorterDuffBO();

        // 实例化混合模式
        porterDuffXfermode = new PorterDuffXfermode(MODE);

        // 计算坐标
        calu(context);
    }

    /**
     * 计算坐标
     *
     * @param context 上下文环境引用
     */
    private void calu(Context context) {
        // 获取包含屏幕尺寸的数组
        int[] screenSize = MeasureUtil.getScreenSize((Activity) context);

        // 获取屏幕尺寸
        screenW = screenSize[0];
        screenH = screenSize[1];

        // 计算左上方正方形原点坐标
        s_l = 0;
        s_t = 0;

        // 计算右上方正方形原点坐标
        d_l = screenW - RECT_SIZE_SMALL;
        d_t = 0;

        // 计算中间方正方形原点坐标
        rectX = screenW / 2 - RECT_SIZE_BIG / 2;
        rectY = RECT_SIZE_SMALL + (screenH - RECT_SIZE_SMALL) / 2 - RECT_SIZE_BIG / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 设置画布颜色为黑色以便我们更好地观察
        canvas.drawColor(Color.BLACK);

        // 设置业务对象尺寸值计算生成左右上方的渐变方形
        porterDuffBO.setSize(RECT_SIZE_SMALL);

		/*
         * 画出左右上方两个正方形
		 * 其中左边的的为src右边的为dis
		 */
        canvas.drawBitmap(porterDuffBO.initSrcBitmap(), s_l, s_t, mPaint);
        canvas.drawBitmap(porterDuffBO.initDisBitmap(), d_l, d_t, mPaint);

		/*
         * 将绘制操作保存到新的图层（更官方的说法应该是离屏缓存）我们将在1/3中学习到Canvas的全部用法这里就先follow me
		 */
        int sc = canvas.saveLayer(0, 0, screenW, screenH, null, Canvas.ALL_SAVE_FLAG);

        // 重新设置业务对象尺寸值计算生成中间的渐变方形
        porterDuffBO.setSize(RECT_SIZE_BIG);

        // 先绘制dis目标图
        canvas.drawBitmap(porterDuffBO.initDisBitmap(), rectX, rectY, mPaint);

        // 设置混合模式
        mPaint.setXfermode(porterDuffXfermode);

        // 再绘制src源图
        canvas.drawBitmap(porterDuffBO.initSrcBitmap(), rectX, rectY, mPaint);

        // 还原混合模式
        mPaint.setXfermode(null);

        // 还原画布
        canvas.restoreToCount(sc);
    }
}
