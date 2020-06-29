package com.tianshaokai.app.view.viewgroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.tianshaokai.app.R;
import com.tianshaokai.app.utils.Logger;

public class FlowLayout extends ViewGroup {

    private static final String TAG = "FlowLayout";

    private int VERTICAL_SPACE = 10;
    private int HORIZONTAL_SPACE = 10;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        if (attrArray != null) {
            HORIZONTAL_SPACE = attrArray.getDimensionPixelSize(R.styleable.FlowLayout_flow_horizontal_space, 0);
            VERTICAL_SPACE = attrArray.getDimensionPixelSize(R.styleable.FlowLayout_flow_vertical_space, 0);
            attrArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //最终的宽和高
        int width = 0;
        int height = 0;

        //设置每一行的宽和高, 取最大值
        int lineWidth = 0;
        //累计 加到height
        int lineHeight = 0;

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        Logger.d(TAG, "paddingLeft: " + paddingLeft + " , paddingTop: " + paddingTop);


        //获取孩子view 个数
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            //测量每一个孩子的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            //获取子view 的layoutparams
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            Logger.d(TAG, "i: " + i + " , child.getMeasuredWidth(): " + child.getMeasuredWidth() + " , child.getMeasuredHeight(): " + child.getMeasuredHeight());

            //当前实际控件占据的宽度
            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin + HORIZONTAL_SPACE;
            //当前实际控件占据的高度
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin + VERTICAL_SPACE;



//            //加入的child 超过了view 宽度就把 view 最大宽度记录下来，累计view 的最大高度
//            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
//                width = Math.max(lineWidth, childWidth);//取最大值记录view的宽度
//                lineWidth = childWidth;//重新开启新的一行开始记录
//
//                height += lineHeight;   //累计最大高度
//
//                lineHeight = childHeight;//开始记录下一行的高度
//
//                //开始计算左上右下 距离 view 左上角的距离
//                int left = paddingLeft;
//                int top = paddingTop + height;
//                int right = childWidth + left - HORIZONTAL_SPACE;
//                int bottom = height + child.getMeasuredHeight() + top;
//
//                child.setTag(new Location(left, top, right, bottom));
//            } else {
//                //开始计算左上右下 距离 view 左上角的距离
//                int left = lineWidth + paddingLeft;
//                int top = paddingTop + height;
//                int right = lineWidth + childWidth + left - HORIZONTAL_SPACE;
//                int bottom = height + child.getMeasuredHeight() + top;
//
//                child.setTag(new Location(left, top, right, bottom));
//
//                lineWidth += childWidth;
//
//                lineHeight = Math.max(lineHeight, childHeight);
//            }


            if (lineWidth + childWidth < widthSize) {
                lineWidth += childWidth;
                height = Math.max(height, childHeight);
                width = Math.max(lineWidth, width);
            } else {
                width = Math.max(lineWidth, width);
                height += childHeight;
                lineWidth = childWidth;
            }
            int left = lineWidth - childWidth + layoutParams.leftMargin;
            int top = height - childHeight + layoutParams.topMargin;
            int right = lineWidth - layoutParams.rightMargin;
            int bottom = height - layoutParams.bottomMargin;

            Location location = new Location(left, top, right, bottom);
            child.setTag(location);
        }

        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize : width, (heightMode == MeasureSpec.EXACTLY) ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            Location location = (Location) child.getTag();
            child.layout(location.left, location.top, location.right, location.bottom);
        }
    }

    public class Location {
        Location(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
        int left;
        int top;
        int right;
        int bottom;
    }
}
