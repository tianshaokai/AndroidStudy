package com.tianshaokai.app.ui;

import com.tianshaokai.app.R;
import com.tianshaokai.app.view.loading2.LoadingView;

import butterknife.BindView;

public class Loading2ViewActivity extends BaseActivity {

    @BindView(R.id.loadingView)
    LoadingView loadingView;

    @Override
    protected int initLayoutViewID() {
        return R.layout.activity_loading2_view;
    }


    @Override
    protected void init() {
        loadingView.startAnimation();
    }

    @Override
    protected void onDestroy() {
        loadingView.stopAnimation();
        super.onDestroy();
    }
}
