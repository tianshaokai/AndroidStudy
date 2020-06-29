package com.tianshaokai.canvasdemo.customviewdemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

import com.tianshaokai.canvasdemo.R;

/**
 * LayerView
 * link(http://blog.csdn.net/aigestudio/article/details/42677973)
 * @author Aige
 * @since 2014/12/15
 */
public class LayerView extends View {
	private Bitmap mBitmap;// 位图对象

	private int mViewWidth, mViewHeight;// 控件宽高

	public LayerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 从资源中获取位图对象
		mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.z);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		/*
		 * 获取控件宽高
		 */
		mViewWidth = w;
		mViewHeight = h;

		// 缩放位图与控件一致
		mBitmap = Bitmap.createScaledBitmap(mBitmap, mViewWidth, mViewHeight, true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		Matrix matrix = new Matrix();
		matrix.setScale(0.8F, 0.35F);
		matrix.postTranslate(100, 100);
		canvas.setMatrix(matrix);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		canvas.restore();
	}
}
