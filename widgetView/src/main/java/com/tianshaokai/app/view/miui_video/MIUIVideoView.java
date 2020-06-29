package com.tianshaokai.app.view.miui_video;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MIUIVideoView extends View {
    private final static String TAG = "MIUIVideoView";
    //控件中心点坐标
    private int cvX, cvY;
    //画笔
    private Paint myPaint;
    //绘制三角形的路径
    private Path mPath;
    //存放三角形的数组，一共有4个三角形
    private Triangle[] triangles = new Triangle[4];
    //绘制状态，用来标记当前应该绘制哪个三角形
    private STATUS currentStatus = STATUS.MID_LOADING;
    //绘制动画
    private ValueAnimator valueAnimator;

    public MIUIVideoView(Context context) {
        super(context);
        init();
    }

    public MIUIVideoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MIUIVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始画笔和路径
        myPaint = new Paint();
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setAntiAlias(true);
        mPath = new Path();
    }

    //枚举变量，存放绘制状态
    private enum STATUS {
        MID_LOADING,
        FIRST_LOADING,
        SECOND_LOADING,
        THIRD_LOADING,
        LOADING_COMPLETE,
        THIRD_DISMISS,
        FIRST_DISMISS,
        SECOND_DISMISS,
        MID_DISMISS
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure is run");

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged is run");

        cvX = getMeasuredWidth() / 2;
        cvY = getMeasuredHeight() / 2;
        initTriangle();
    }

    private void initTriangle() {
        currentStatus = STATUS.MID_LOADING;
        Triangle triangle = new Triangle();
        //offset就是CD的长度，利用勾股定理
        //三角形边长
        int edge = 200;
        int offset = (int) Math.sqrt(Math.pow(edge, 2) - Math.pow(edge / 2f, 2));
        triangle.startX = cvX + offset / 2;
        triangle.startY = cvY + edge / 2;
        triangle.endX1 = cvX + offset / 2;
        triangle.endY1 = cvY - edge / 2;
        triangle.endX2 = cvX - offset / 2;
        triangle.endY2 = cvY;
        //current为延伸中的实时坐标，默认在起始点位置
        triangle.currentX1 = triangle.startX;
        triangle.currentY1 = triangle.startY;
        triangle.currentX2 = triangle.startX;
        triangle.currentY2 = triangle.startY;
        triangle.color = "#be8cd5";
        triangles[0] = triangle;
        //计算第一个三角形的坐标位置
        Triangle firstTriangle = new Triangle();
        firstTriangle.startX = triangle.endX2;
        firstTriangle.startY = triangle.endY2;
        firstTriangle.endX1 = triangle.endX1;
        firstTriangle.endY1 = triangle.endY1;
        firstTriangle.endX2 = firstTriangle.startX;
        firstTriangle.endY2 = firstTriangle.startY - edge;
        firstTriangle.color = "#fcb131";
        triangles[1] = firstTriangle;
        //计算第二个三角形的坐标位置
        Triangle secondTriangle = new Triangle();
        secondTriangle.startX = triangle.endX1;
        secondTriangle.startY = triangle.endY1;
        secondTriangle.endX1 = secondTriangle.startX;
        secondTriangle.endY1 = secondTriangle.startY + edge;
        secondTriangle.endX2 = secondTriangle.startX + offset;
        secondTriangle.endY2 = secondTriangle.startY + edge / 2;
        secondTriangle.color = "#67c6ca";
        triangles[2] = secondTriangle;
        //计算第三个三角形的坐标位置
        Triangle thirdTriangle = new Triangle();
        thirdTriangle.startX = triangle.startX;
        thirdTriangle.startY = triangle.startY;
        thirdTriangle.endX1 = triangle.endX2;
        thirdTriangle.endY1 = triangle.endY2;
        thirdTriangle.endX2 = triangle.endX2;
        thirdTriangle.endY2 = thirdTriangle.endY1 + edge;
        thirdTriangle.color = "#eb7583";
        triangles[3] = thirdTriangle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Triangle triangle : triangles) {
            mPath.reset();
            //移动到当前三角形的起始点位置上
            mPath.moveTo(triangle.startX, triangle.startY);
            //连接目前的current1
            mPath.lineTo(triangle.currentX1, triangle.currentY1);
            //连接目前的current2
            mPath.lineTo(triangle.currentX2, triangle.currentY2);
            //三角形线段闭合
            mPath.close();
            //设置三角形颜色
            myPaint.setColor(Color.parseColor(triangle.color));
            //绘制三角形
            canvas.drawPath(mPath, myPaint);
            //当只绘制中间三角形时，其他三角形不需要进行绘制
            if (currentStatus == STATUS.MID_LOADING) {
                break;
            }
        }
    }

    public void startTranglesAnimation() {
        //初始化三角形位置
//        initTriangle();
        //如果有动画已经在执行了，取消当前执行的动画。
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        //动画插值从0变成1
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        //每次动画的执行时长为300毫秒
        valueAnimator.setDuration(3000);
        //无限次执行
        valueAnimator.setRepeatCount(-1);
        //每次执行的方案都是从头开始
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        //监听每次动画的循环情况，没循环一次进入下一个阶段
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.e(TAG, "onAnimationRepeat currentStatus: " + currentStatus);
                //当上一个动画状态执行完之后进入下一个阶段。
                if (currentStatus == STATUS.MID_LOADING) {
                    currentStatus = STATUS.FIRST_LOADING;
                } else if (currentStatus == STATUS.FIRST_LOADING) {
                    currentStatus = STATUS.SECOND_LOADING;
                } else if (currentStatus == STATUS.SECOND_LOADING) {
                    currentStatus = STATUS.THIRD_LOADING;
                } else if (currentStatus == STATUS.THIRD_LOADING) {
                    currentStatus = STATUS.LOADING_COMPLETE;
                    reverseTriangleStart();
                } else if (currentStatus == STATUS.LOADING_COMPLETE) {
                    currentStatus = STATUS.THIRD_DISMISS;
                } else if (currentStatus == STATUS.THIRD_DISMISS) {
                    currentStatus = STATUS.FIRST_DISMISS;
                } else if (currentStatus == STATUS.FIRST_DISMISS) {
                    currentStatus = STATUS.SECOND_DISMISS;
                } else if (currentStatus == STATUS.SECOND_DISMISS) {
                    currentStatus = STATUS.MID_DISMISS;
                } else if (currentStatus == STATUS.MID_DISMISS) {
                    Log.e(TAG, "onAnimationRepeat");
                    currentStatus = STATUS.MID_LOADING;
                    reverseTriangleStart();
                }
            }
        });
        //监听动画执行过程
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //或者目前的插值(0-1)
                float fraction = animation.getAnimatedFraction();
                //如果目前的动画是消失状态，则插值正好是反过来的，是1-0，所以需要用1-fraction
                if (currentStatus == STATUS.FIRST_DISMISS || currentStatus == STATUS.SECOND_DISMISS ||
                        currentStatus == STATUS.THIRD_DISMISS || currentStatus == STATUS.MID_DISMISS) {
                    fraction = 1 - fraction;
                }
                //根据目前执行的状态，取出对应的需要处理的三角形
                Triangle triangle = null;
                if (currentStatus == STATUS.MID_LOADING || currentStatus == STATUS.MID_DISMISS) {
                    triangle = triangles[0];
                } else if (currentStatus == STATUS.FIRST_LOADING || currentStatus == STATUS.FIRST_DISMISS) {
                    triangle = triangles[1];
                } else if (currentStatus == STATUS.SECOND_LOADING || currentStatus == STATUS.SECOND_DISMISS) {
                    triangle = triangles[2];
                } else if (currentStatus == STATUS.THIRD_LOADING || currentStatus == STATUS.THIRD_DISMISS) {
                    triangle = triangles[3];
                } else if (currentStatus == STATUS.LOADING_COMPLETE) {
                    //如果是LOADING_COMPLETE状态的话，此次动画效果保持不变
//                        invalidate();
                    return;
                }
                if (triangle != null) {
                    //这里是三角形变化的过程，计算目前current的坐标应当处在什么位置上
                    //当fration为0的时候，current的坐标为start位置，当fratcion为1的时候，current的坐标是end位置
                    triangle.currentX1 = (int) (triangle.startX + fraction * (triangle.endX1 - triangle.startX));
                    triangle.currentY1 = (int) (triangle.startY + fraction * (triangle.endY1 - triangle.startY));
                    triangle.currentX2 = (int) (triangle.startX + fraction * (triangle.endX2 - triangle.startX));
                    triangle.currentY2 = (int) (triangle.startY + fraction * (triangle.endY2 - triangle.startY));

                    invalidate();
                } else {
                    Log.e(TAG, "triangle is null");
                }
            }
        });

        valueAnimator.start();
    }

    private void reverseTriangleStart() {
        for (Triangle triangle : triangles) {
            int startX = triangle.startX;
            int startY = triangle.startY;
            triangle.startX = triangle.endX1;
            triangle.startY = triangle.endY1;
            triangle.endX1 = startX;
            triangle.endY1 = startY;
            triangle.currentX1 = triangle.endX1;
            triangle.currentY1 = triangle.endY1;
        }
    }

    public void stopAnimation() {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }
}
