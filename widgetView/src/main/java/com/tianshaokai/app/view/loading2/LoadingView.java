package com.tianshaokai.app.view.loading2;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

public class LoadingView extends View implements ValueAnimator.AnimatorUpdateListener {

    private LoadingDrawable loadingDrawable;

    private ValueAnimator valueAnimator = new ValueAnimator();

    private long duration = 4000;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadingDrawable = new LoadingDrawable();
        loadingDrawable.setBallSize(4);
        loadingDrawable.setBallRadius(50);
        loadingDrawable.setReverseGravity(Gravity.TOP);
        loadingDrawable.setReverseWeight(0.6F);
        loadingDrawable.setBallColor(0xFFFF3399);
        loadingDrawable.setBallReverseColor(0xFF009999);
        loadingDrawable.setInterpolatorFactor(0.9F);

        valueAnimator.setFloatValues(0,1);
        valueAnimator.addUpdateListener(this);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);

        setBackground(loadingDrawable);

        if(isInEditMode()){
            loadingDrawable.onProgressChange(0.5f);
        }
    }

    public void startAnimation(){
        valueAnimator.start();
    }

    public void stopAnimation(){
        valueAnimator.cancel();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        loadingDrawable.onProgressChange((Float) animation.getAnimatedValue());
    }
}
