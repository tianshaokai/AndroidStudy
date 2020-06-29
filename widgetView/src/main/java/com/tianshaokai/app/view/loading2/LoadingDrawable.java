package com.tianshaokai.app.view.loading2;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;


public class LoadingDrawable extends Drawable {

    /**
     * 画笔，用来控制绘制的颜色，样式等
     */
    private Paint paint = new Paint();

    /**
     * 进度，用来控制动画本身的状态，以此确定动画中元素的位置
     */
    private float progress = 0;

    /**
     * 最大透明度
     */
    private int maxAlpha = 255;

    /**
     * 最小透明度
     */
    private int minAlpha = 128;

    /**
     * 小球位置的集合
     */
    private ArrayList<PointF> ballList = new ArrayList<>();

    /**
     * 反向转圈的小球集合
     */
    private ArrayList<PointF> ballListReverse = new ArrayList<>();

    /**
     * 小球的半径
     */
    private float ballRadius = 0F;

    /**
     * 差速器因子
     */
    private float interpolatorFactor = 1.0F;

    /**
     * 尾巴的权重
     */
    private float tailWeight = 0.9F;

    /**
     * 尾巴的速度
     */
    private float tailSpeed = 0.0F;

    /**
     * 逆向小圈的尺寸比例
     */
    private float reverseWeight = 0.9F;

    /**
     * 逆向小圈的相对位置
     */
    private int reverseGravity = Gravity.CENTER;

    /**
     * 小球的颜色
     */
    private int ballColor = Color.BLACK;

    /**
     * 反向小球的颜色
     */
    private int ballReverseColor = Color.BLACK;

    public LoadingDrawable(){
    }

    /**
     * 绘制的方法
     * @param canvas
     */
    @Override
    public void draw(@NonNull Canvas canvas) {

        if(ballListReverse.size() > 0){
            paint.setColor(ballReverseColor);
            int alphaStep = (maxAlpha - minAlpha) / ballListReverse.size();
            for(int i = 0,count = ballListReverse.size(); i < count; i++){
                PointF ball = ballListReverse.get(i);
                paint.setAlpha(alphaStep * i + minAlpha);
                canvas.drawCircle(ball.x,ball.y, (float) (ballRadius * Math.pow(tailWeight, count - i)),paint);
            }
        }

        if(ballList.size() > 0){
            paint.setColor(ballColor);
            int alphaStep = (maxAlpha - minAlpha) / ballList.size();
            for(int i = 0,count = ballList.size(); i < count; i++){
                PointF ball = ballList.get(i);
                paint.setAlpha(alphaStep * i + minAlpha);
                canvas.drawCircle(ball.x,ball.y, (float) (ballRadius * Math.pow(tailWeight, count - i)),paint);
            }
        }

    }

    /**
     * 为画板设置透明度，我们把它绑定到画笔上
     * @param alpha
     */
    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    /**
     * 颜色过滤器
     * @param colorFilter
     */
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }


    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    /**
     * 外界控制进度的方法，修改进度后，发起重绘
     * @param pro
     */
    public void onProgressChange(@FloatRange(from = 0,to = 1) float pro){
        this.progress = pro;
        changeLocation();
    }

    public void setBallSize(int size){
        setBallSize(size,size);
    }
    /**
     * 设置小球的数量
     * @param size
     */
    public void setBallSize(int size,int sizeReverse){
        //如果现有小球过多，那么移除多余的
        while (size >= 0 && ballList.size() > size){
            ballList.remove(0);
        }
        while (sizeReverse >= 0 && ballListReverse.size() > sizeReverse){
            ballListReverse.remove(0);
        }
        //如果当前小球过少，那么添加满
        while (ballList.size() < size){
            ballList.add(new PointF());
        }
        while (ballListReverse.size() < sizeReverse){
            ballListReverse.add(new PointF());
        }
        changeLocation();
    }

    public void setMaxAlpha(int maxAlpha) {
        this.maxAlpha = maxAlpha;
    }

    public void setMinAlpha(int minAlpha) {
        this.minAlpha = minAlpha;
    }

    public void setTailWeight(float tailWeight) {
        this.tailWeight = tailWeight;
    }

    public void setTailSpeed(float tailSpeed) {
        this.tailSpeed = tailSpeed;
    }

    public void setInterpolatorFactor(float interpolatorFactor) {
        this.interpolatorFactor = interpolatorFactor;
    }

    public void setReverseWeight(float reverseWeight) {
        this.reverseWeight = reverseWeight;
    }

    public void setReverseGravity(int reverseGravity) {
        this.reverseGravity = reverseGravity;
    }

    public void setBallColor(int ballColor) {
        this.ballColor = ballColor;
    }

    public void setBallReverseColor(int ballReverseColor) {
        this.ballReverseColor = ballReverseColor;
    }

    public void setBallRadius(float ballRadius) {
        this.ballRadius = ballRadius;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        changeLocation();
    }

    /**
     * 改变位置，内部方法，根据当前进度，修改每个小球的坐标
     */
    private void changeLocation(){

        // 顺时针
        float angle = progress * 360;
        float centerX = getBounds().centerX();
        float centerY = getBounds().centerY();
        float radius = Math.min(getBounds().width(),getBounds().height()) / 2 - ballRadius;
        float lastAngle = angle;
        for(PointF point: ballList){
            float newAngle = interpolator(angle);
            float[] loc = getLocation(centerX,centerY,speedTo(lastAngle,angle),radius);
            point.set(loc[0],loc[1]);
            lastAngle = angle;
            angle = newAngle;
        }

        // 逆时针
        angle = progress * 360;
        lastAngle = angle;

        float left = centerX - radius;
        float right = centerX + radius;
        float top = centerY - radius;
        float bottom = centerY + radius;

        radius *= reverseWeight;
        if((reverseGravity & Gravity.LEFT) == Gravity.LEFT){
            centerX = left + radius;
        } else if((reverseGravity & Gravity.RIGHT) == Gravity.RIGHT){
            centerX = right - radius;
        }
        if((reverseGravity & Gravity.TOP) == Gravity.TOP){
            centerY = top + radius;
        } else if((reverseGravity & Gravity.BOTTOM) == Gravity.BOTTOM){
            centerY = bottom - radius;
        }

        for(PointF point: ballListReverse){
            float newAngle = interpolator(angle);
            float[] loc = getLocation(centerX,centerY,speedToReverse(lastAngle,angle),radius);
            point.set(loc[0],loc[1]);
            lastAngle = angle;
            angle = newAngle;
        }

        invalidateSelf();
    }

    /**
     * 计算尾巴的速度比，即在前一个圆的速度和前一个圆的位置间取值，
     * 当速度比为1时，所有圆的速度相同
     */
    private float speedTo(float lastAngle,float nowAngle){
        if(lastAngle % 360 == nowAngle % 360 && nowAngle == 0){
            return 0;
        }
        return (lastAngle - nowAngle) * tailSpeed + nowAngle;
    }

    private float speedToReverse(float lastAngle,float nowAngle){
        return 360 - speedTo(lastAngle, nowAngle);
    }

    private float[] getLocation(float centerX,float centerY, float angle,float radius){
        float[] loc = {0,0};
        //用360度取余，去掉多余的角度
        angle %= 360;

        loc[0] = (float) (Math.sin(2 * Math.PI / 360 * angle) * radius);
        loc[1] = (float) (-Math.cos(2 * Math.PI / 360 * angle) * radius);

        //因为上述的角度计算是基于原点坐标的，因此此处偏移到圆心位置
        loc[0] += centerX;
        loc[1] += centerY;
        return loc;
    }

    private float interpolator(float angle){
        //算法是：对360取余，得到一圈内的度数，然后除以360，得到当前度数的进度比，最后将结果转换为度数
        return accelerateDecelerateInterpolator(angle % 360 / 360) * 360;
    }

    /**
     * 先加速，再减速的插值器
     */
    private float accelerateDecelerateInterpolator(float x){
        float result;
        if (interpolatorFactor == 1.0f) {
            result = (1.0f - (1.0f - x) * (1.0f - x));
        } else {
            result = (float)(1.0f - Math.pow((1.0f - x), 2 * interpolatorFactor));
        }
        return result;
    }

}

