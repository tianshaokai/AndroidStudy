package com.tianshaokai.canvasdemo.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tianshaokai.canvasdemo.R;

/**
 * Created by tianshaokai on 2016/3/11.
 */
public class MyDraw extends View {

    private Bitmap mBitmap;
    private Paint mPaint;
    private Rect mSrcRect, mDestRect;

    public MyDraw(Context context) {
        super(context);
        init();
    }

    public MyDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.world);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSrcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mDestRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Log.d("Draw", "ACTION_MOVE 方法执行");
            //    mSrcRect.set(getWidth(), getHeight(), m);
            break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, mSrcRect, mDestRect, mPaint);
    }
}
