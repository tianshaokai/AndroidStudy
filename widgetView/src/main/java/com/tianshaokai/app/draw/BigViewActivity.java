package com.tianshaokai.app.draw;

import com.tianshaokai.app.R;
import com.tianshaokai.app.ui.BaseActivity;
import com.tianshaokai.app.view.BigView;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;

public class BigViewActivity extends BaseActivity {

    @BindView(R.id.bitView)
    BigView bigView;

    @Override
    protected int initLayoutViewID() {
        return R.layout.activity_big_view;
    }

    @Override
    protected void init() {
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("a.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        bigView.setImage(inputStream);
    }
}
