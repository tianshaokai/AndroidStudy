package com.tianshaokai.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.tianshaokai.app.utils.Logger;

import java.io.IOException;
import java.io.InputStream;


public class BigView extends View implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private static final String TAG = "BigView";

    private BitmapFactory.Options mOptions;
    private BitmapRegionDecoder mBitmapRegionDecoder;
    //加载的图片大小
    private Rect mRect;
    //滚动的处理类
    private Scroller mScroller;
    //手势处理类
    private GestureDetector mGestureDetector;
    //图片的宽高
    private int mImageWidth, mImageHeight;
    //View的宽高
    private int mViewWidth, mViewHeight;
    private Bitmap mBitmap;
    private float mScale;


    public BigView(Context context) {
        this(context, null);
    }

    public BigView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     * @param context
     */
    private void init(Context context) {
        mRect = new Rect();
        mOptions = new BitmapFactory.Options();
        mGestureDetector = new GestureDetector(context, this);
        mScroller = new Scroller(context);
        setOnTouchListener(this);
    }


    /**
     * 设置图片
     * @param is
     */
    public void setImage(InputStream is) {
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, mOptions);
        mImageWidth = mOptions.outWidth;
        mImageHeight = mOptions.outHeight;

        //开启复用
        mOptions.inMutable = true;
        mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        mOptions.inJustDecodeBounds = false;
        //创建区域图片解码器
        try {
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();


        Logger.d(TAG, "mViewWidth: " + mViewWidth +  ", mViewHeight: " + mViewHeight + ", mImageWidth: " + mImageWidth);


        //确定图片加载区域
        mRect.left = 0;
        mRect.top = 0;
        mRect.right = mImageWidth;
        //先获取图片等比缩放因子
//        mScale = mViewWidth / mImageWidth;
//        mRect.bottom = (int) (mImageHeight / mScale);
        mRect.bottom = mImageHeight;
        //计算图片高度

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapRegionDecoder == null) {
            Log.d(TAG, "");
            return;
        }

        mOptions.inBitmap = mBitmap;
        mBitmap = mBitmapRegionDecoder.decodeRegion(mRect, mOptions);
        //获取矩阵缩放图片
//        Matrix matrix = new Matrix();
//        matrix.setScale(mScale, mScale);
        canvas.drawBitmap(mBitmap, mRect, mRect, null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    //处理按下事件
    @Override
    public boolean onDown(MotionEvent e) {
        //如果没有停止就停止滑动
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
        //继续接收后续事件
        return true;
    }

    //处理滑动事件
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }



    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
