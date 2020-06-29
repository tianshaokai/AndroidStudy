package com.tianshaokai.canvasdemo.pagecurl.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.tianshaokai.canvasdemo.R;
import com.tianshaokai.canvasdemo.pagecurl.views.CurveView;
import com.tianshaokai.canvasdemo.pagecurl.views.FoldView;
import com.tianshaokai.canvasdemo.pagecurl.views.PageTurnView;
import com.tianshaokai.canvasdemo.pagecurl.views.TwistView;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用主界面
 *
 * @author AigeStudio
 * @since 2014/12/15
 * @version 1.0.0
 *
 */
public class PageCurlActivity extends Activity {
	private PageTurnView mPageCurlView;// 翻页控件
	private FoldView mFoldView;// 折页控件
	private CurveView mCurveView;// 曲线控件
	private TwistView mTwistView;// 曲线控件

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curl_page);

		twistPage();

		//curvePage();

		 //foldPage();

		//turnPage();
	}

	private void twistPage() {
		mTwistView = (TwistView) findViewById(R.id.main);
		mTwistView.setBitmaps(initBitmaps());
	}

	private void curvePage() {
		mCurveView = (CurveView) findViewById(R.id.main);
		mCurveView.setBitmaps(initBitmaps());
	}

	private void foldPage() {
		mFoldView = (FoldView) findViewById(R.id.main);
		mFoldView.setBitmaps(initBitmaps());
	}

	private void turnPage() {
		mPageCurlView = (PageTurnView) findViewById(R.id.main);
		mPageCurlView.setBitmaps(initBitmaps());
	}

	private List<Bitmap> initBitmaps() {
		Bitmap bitmap = null;
		List<Bitmap> bitmaps = new ArrayList<Bitmap>();

		bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_a);
		bitmaps.add(bitmap);
		bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_b);
		bitmaps.add(bitmap);
		bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_c);
		bitmaps.add(bitmap);
		bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_d);
		bitmaps.add(bitmap);
		bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.page_img_e);
		bitmaps.add(bitmap);

		return bitmaps;
	}

	@Override
	protected void onDestroy() {
		if (null != mFoldView) {
			mFoldView.slideStop();
			mFoldView.getSlideHandler().removeCallbacksAndMessages(null);
		}
		if (null != mCurveView) {
			mCurveView.slideStop();
			mCurveView.getSlideHandler().removeCallbacksAndMessages(null);
		}
		super.onDestroy();
	}
}
