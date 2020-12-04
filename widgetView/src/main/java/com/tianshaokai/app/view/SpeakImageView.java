package com.tianshaokai.app.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.tianshaokai.app.R;

/**
 * description:
 * author: KisenHuang
 * email: KisenHuang@163.com
 * time: 2018/8/13 上午10:31
 */

public class SpeakImageView extends FrameLayout implements View.OnClickListener {

    ImageView ivSpeak;
    View viewAnimBg;

    View frameLayout;

    private OnImageTouchListener mOnImageTouchListener;
    private long touchMillis;
    boolean speaking = false;
    private ObjectAnimator rotateAnimotor;

    AnimatorSet animatorSet, recorderAnimSet;




    public SpeakImageView(Context context) {
        this(context, null);
    }

    public SpeakImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeakImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.eng_common_speak_view_layout, this);
        ivSpeak = findViewById(R.id.ivSpeak);
        viewAnimBg = findViewById(R.id.viewAnimBg);
        ivSpeak.setOnClickListener(this);
//        if (context instanceof OnImageTouchListener) {
//            mOnImageTouchListener = (OnImageTouchListener) context;
//        }
    }

    @Override
    public void onClick(View v) {
        if (!speaking) {
            startSpeak();
            mOnImageTouchListener.onSpeakStart();
        } else {
            stopSpeak();
            mOnImageTouchListener.onSpeakUp(System.currentTimeMillis() - touchMillis);
        }
    }

    private void startSpeak() {
        speaking = true;
        ivSpeak.setImageResource(R.mipmap.eng_ic_record);
        touchMillis = System.currentTimeMillis();

        startRecordIconAnim();
    }

    public void stopSpeak() {
        speaking = false;
        ivSpeak.setImageResource(R.mipmap.eng_ic_speak);
        cancelRecordIconAnim();
    }

    public void startRecordIconAnim() {
        cancelRecordIconAnim();
//        rotateAnimotor = ExampleAnimUtil.centerRotateAnim(ivSpeak, -20,20, ValueAnimator.REVERSE, ValueAnimator.INFINITE, 1000);
//        rotateAnimotor.start();

    }

    public void cancelRecordIconAnim() {
        if (rotateAnimotor != null) {
            rotateAnimotor.end();
            rotateAnimotor.cancel();
        }
        ivSpeak.setRotation(0);
        rotateAnimotor=null;
    }

    public void setOnImageTouchListener(OnImageTouchListener mOnImageTouchListener) {
        this.mOnImageTouchListener = mOnImageTouchListener;
    }

    public interface OnImageTouchListener {

        void onSpeakStart();

        void onSpeakUp(long s);
    }
}
