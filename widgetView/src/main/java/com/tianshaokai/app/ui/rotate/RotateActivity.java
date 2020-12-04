package com.tianshaokai.app.ui.rotate;

import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.tianshaokai.app.R;
import com.tianshaokai.app.ui.BaseActivity;

import butterknife.BindView;

public class RotateActivity extends BaseActivity {

    @BindView(R.id.ivLightBeam)
    ImageView imageView;

    @Override
    protected int initLayoutViewID() {
        return R.layout.activity_rotate;
    }

    @Override
    protected void init() {
        super.init();

        imageView.setAnimation(rotateLightBeam());
    }

    private Animation rotateLightBeam() {
        RotateAnimation animation;
        int magnify = 10000;
        int toDegrees = 360;
        int duration = 5000;
        toDegrees *= magnify;
        duration *= magnify;
        animation = new RotateAnimation(0, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        LinearInterpolator lir = new LinearInterpolator();
        animation.setInterpolator(lir);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        return animation;
    }
}
