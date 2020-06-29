package com.tianshaokai.canvasdemo.customviewdemo.views;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * ��ҳView
 * 
 * @author Aige
 * @since 2014/12/17
 */
public class PageView extends View {
	private List<Bitmap> mBitmaps;// λͼ�����б�

	public PageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {

	}
}
