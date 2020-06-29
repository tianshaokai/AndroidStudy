package com.tianshaokai.app.ui;

import android.view.View;

import com.tianshaokai.app.R;
import com.tianshaokai.app.view.miui_video.MIUIVideoView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 小米 MIUI 视频播放器 loading
 */
public class MiUiVideoActivity extends BaseActivity {

    @BindView(R.id.miuiVideoView)
    MIUIVideoView miuiVideoView;

    @Override
    protected int initLayoutViewID() {
        return R.layout.activity_miui_video_view;
    }

    @OnClick({R.id.start})
    public void onViewClicked(View view) {
        miuiVideoView.startTranglesAnimation();
    }

    @Override
    protected void onDestroy() {
        miuiVideoView.stopAnimation();
        super.onDestroy();
    }
}
