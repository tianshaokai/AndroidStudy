package com.tianshaokai.canvasdemo.customviewdemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * CanvasView
 *
 * @author Aige
 * @since 2014/12/12
 */
public class PathView extends View {
	private Path mPath;// 路径对象
	private Paint mPaint;// 路径画笔对象
	private TextPaint mTextPaint;// 文本画笔对象

	public PathView(Context context, AttributeSet attrs) {
		super(context, attrs);

		/*
		 * 实例化画笔并设置属性
		 */
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.CYAN);
		mPaint.setStrokeWidth(5);

		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setColor(Color.DKGRAY);
		mTextPaint.setTextSize(20);

		// 实例化路径
		mPath = new Path();

		// 添加一条弧线到Path中
		RectF oval = new RectF(100, 100, 300, 400);
		mPath.addOval(oval, Path.Direction.CCW);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 绘制路径
		canvas.drawPath(mPath, mPaint);

		// 绘制路径上的文字
		canvas.drawTextOnPath("ad撒发射点发放士大夫斯蒂芬斯蒂芬森啊打扫打扫打扫达发达省份撒旦发射的", mPath, 0, 0, mTextPaint);
	}
}
