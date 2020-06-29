package com.tianshaokai.app.ui;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tianshaokai.app.R;
import com.tianshaokai.app.view.viewgroup.FlowLayout;

import butterknife.BindView;

public class FlowActivity extends BaseActivity {

    @BindView(R.id.flowLayout)
    public FlowLayout flowLayout;

    @Override
    protected int initLayoutViewID() {
        return R.layout.layout_flow;
    }

    @Override
    protected void init() {

        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        String[] string = {"从我写代码那天起，我就没有打算写代码", "从我写代码那天起", "我就没有打算写代码", "没打算", "写代码"};
        for (int i = 0; i < string.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(string[i]);
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.round_square_blue);
            flowLayout.addView(textView, lp);
        }
    }
}
