package com.tianshaokai.canvasdemo.customviewdemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

/**
 * CanvasView
 *
 * @author Aige
 * @since 2014/12/12
 */
public class CanvasView extends View {
	private Region mRegion;// 区域对象
	private Rect mRect;// 矩形对象
	private Paint mPaint;// 绘制边框的Paint

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 实例化画笔并设置属性
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.DKGRAY);
		mPaint.setStrokeWidth(2);

		// 实例化矩形对象
		mRect = new Rect(0, 0, 200, 200);

		// 实例化区域对象
		mRegion = new Region(200, 200, 400, 400);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 缩放画布
		canvas.scale(0.75F, 0.75F);

		canvas.save();

		// 裁剪矩形
		canvas.clipRect(mRect);
		canvas.drawColor(Color.RED);

		canvas.restore();

		canvas.save();

		// 裁剪区域
		canvas.clipRegion(mRegion);
		canvas.drawColor(Color.RED);

		canvas.restore();

		// 为画布绘制一个边框便于观察
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
	}
}
