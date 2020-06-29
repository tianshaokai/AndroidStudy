package com.tianshaokai.baidudemo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

public class CustomPopupWindow {
    private PopupWindow mPopupWindow;
    private View contentView;
    private Context mContext;

    private CustomPopupWindow(Builder builder) {
        mContext = builder.context;
        contentView = LayoutInflater.from(mContext).inflate(builder.contentViewId, null);
        mPopupWindow = new PopupWindow(contentView, builder.width, builder.height, builder.focus);

        //需要跟 setBackGroundDrawable 结合
        mPopupWindow.setOutsideTouchable(builder.outsideCancel);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setAnimationStyle(builder.animStyle);
        mPopupWindow.setOnDismissListener(builder.onDismissListener);
    }

    /**
     * popup 消失
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 根据id获取view
     *
     * @param viewId
     * @return
     */
    public View findViewById(int viewId) {
        if (mPopupWindow != null) {
            return this.contentView.findViewById(viewId);
        }
        return null;
    }

    /**
     * 根据父布局，显示位置
     *
     * @param rootViewId
     * @param gravity
     * @param x
     * @param y
     * @return
     */
    public CustomPopupWindow showAtLocation(int rootViewId, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            View rootView = LayoutInflater.from(mContext).inflate(rootViewId, null);
            mPopupWindow.showAtLocation(rootView, gravity, x, y);
        }
        return this;
    }

    public CustomPopupWindow showAtLocation(View rootViewId, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(rootViewId, gravity, x, y);
        }
        return this;
    }

    public CustomPopupWindow showAsDropDown(View targetView) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(targetView);
        }
        return this;
    }

    public CustomPopupWindow showAsDropDown(View targetView, int xoff, int yoff) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(targetView, xoff , yoff);
        }
        return this;
    }

    /**
     * 根据id获取view ，并显示在该view的位置
     *
     * @param targetViewId
     * @param gravity
     * @param offx
     * @param offy
     * @return
     */
    public CustomPopupWindow showAsLocation(int targetViewId, int gravity, int offx, int offy) {
        if (mPopupWindow != null) {
            View targetView = LayoutInflater.from(mContext).inflate(targetViewId, null);
            mPopupWindow.showAtLocation(targetView, gravity, offx, offy);
        }
        return this;
    }

    /**
     * 显示在 targetView 的不同位置
     *
     * @param targetView
     * @param gravity
     * @param offx
     * @param offy
     * @return
     */
    public CustomPopupWindow showAsLocation(View targetView, int gravity, int offx, int offy) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(targetView,gravity,offx,offy);
        }
        return this;
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     * @param v
     */
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        mPopupWindow.getContentView().measure(0, 0);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0])+ v.getMeasuredWidth()/2 - contentView.getMeasuredWidth() / 2, location[1] - contentView.getMeasuredHeight());
    }

    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     * @param v
     */
    public void showUp2(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - contentView.getMeasuredWidth() / 2, location[1] - contentView.getMeasuredHeight());
    }

    public boolean isShowing() {
        if (mPopupWindow == null) {
            return false;
        }
        return mPopupWindow.isShowing();
    }

    public int getHeight(){
        if (mPopupWindow == null) {
            return 0;
        }
        return mPopupWindow.getHeight();
    }

    public int getWidth(){
        if (mPopupWindow == null) {
            return 0;
        }
        return mPopupWindow.getWidth();
    }


    /**
     * builder 类
     */
    public static class Builder {
        private int contentViewId;
        private int width;
        private int height;
        private boolean focus;
        private boolean outsideCancel;
        private int animStyle;
        private PopupWindow.OnDismissListener onDismissListener;
        private Context context;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }


        public Builder setContentView(int contentViewId) {
            this.contentViewId = contentViewId;
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setFocus(boolean focus) {
            this.focus = focus;
            return this;
        }

        public Builder setOutSideCancel(boolean outsideCancel) {
            this.outsideCancel = outsideCancel;
            return this;
        }

        public Builder setAnimationStyle(int animStyle) {
            this.animStyle = animStyle;
            return this;
        }

        public Builder setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
            this.onDismissListener = onDismissListener;
            return this;
        }


        public CustomPopupWindow builder() {
            return new CustomPopupWindow(this);
        }
    }
}
