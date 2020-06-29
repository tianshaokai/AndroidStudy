package com.tianshaokai.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class YahooLoadingView extends View {

    private Paint paint;

    private RectF rectF;

    private int width = 360;

    private int progress = 0;

    private int status = 1;


    private int startOne = 0;
    private int startTwo = 180;

    private int endOne = progress;
    private int endTwo = progress;

    public YahooLoadingView(Context context) {
        super(context);
        init();
        initThread();
        initAnimation();
    }

    public YahooLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initThread();
        initAnimation();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);
        paint.setStyle(Paint.Style.STROKE);

        rectF = new RectF(30, 30, width - 30, width - 30);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (progress >= 180) {
                status = -1;
            }
            if (progress <= 0) {
                status = 3;
            }
            progress += status;

            endOne = progress;
            endTwo = progress;


            if (status == -1) {
                startOne = 180 - progress;
            }
            if (startOne >= 180) {
                startOne = 0;
            }

            if (status == -1) {
                startTwo = 360 - progress;
            }
            if (startTwo >= 360) {
                startTwo = 180;
            }

            invalidate();

            return true;
        }
    });

    private void initThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    handler.sendEmptyMessageDelayed(0, 1000);
                    SystemClock.sleep(8);
                }
            }
        }).start();
    }


    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(12000);   //设置动画播放时长
        rotateAnimation.setRepeatCount(Animation.INFINITE); //设置动画循环播放
        rotateAnimation.setInterpolator(new LinearInterpolator());  //设置动画以均匀的速率在改变
        this.startAnimation(rotateAnimation);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rectF, startOne, endOne, false, paint);
        canvas.drawArc(rectF, startTwo, endTwo, false, paint);
    }
}
