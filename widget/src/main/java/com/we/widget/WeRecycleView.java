package com.we.widget;

import android.content.Context;
import android.hardware.SensorManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author : tianshaokai
 * Date   : 2020/06/28 1:55 PM
 * Desc   :
 * Since  :
 */

public class WeRecycleView extends RecyclerView {
    public final static int SCROLL_NONE = 0;
    public final static int SCROLL_SCALE_HEAD = 1;

    private View mHead;
    private Scroller mScaleScroller;
    private Scroller mFlyScroller;
    private Scroller mFillScroller;
    private OverScroller mEdgeScroller;
    private LinearLayoutManager mLayoutManager;
    private Interpolator mInterpolator;
    private int mLastY;
    private int mDy;
    private int mMode;
    private boolean isDraging;
    private final static int MODE_SCALE = 1;
    private final static int MODE_TOUCH = 2;
    private final static int MODE_FLING = 3;
    private final static int MODE_IDLE = 4;
    private int mScrollMode;
    private com.we.widget.WeRecycleView.WeScroller mWeScroller;
    private VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private int mPointerId;
    private int mActionIndex;
    private int mCloseOffset = 200;
    private int mMinTop;
    private float mMaxScale = 4f;
    private int mSafeHeight;
    private com.we.widget.WeRecycleView.OnHeadExpandListener mListener;
    private com.we.widget.WeRecycleView.OnLayoutListener mLayoutListener;
    private float mExpandPercent;
    private int mSecondViewTop = Integer.MIN_VALUE;
    private final int MIN_SCALE_VY = (int) (getResources().getDisplayMetrics().density * 150);
    private final int MAX_SCALE_VY = (int) (getResources().getDisplayMetrics().density * 300);


    public WeRecycleView(Context paramContext) {
        this(paramContext, null);
    }

    public WeRecycleView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }


    private void init() {
        mInterpolator = new DecelerateInterpolator();
        mScaleScroller = new Scroller(getContext(), mInterpolator);
        mFlyScroller = new Scroller(getContext(), mInterpolator);
        mFillScroller = new Scroller(getContext(), mInterpolator);
        mEdgeScroller = new OverScroller(getContext(), mInterpolator);

        mMinFlingVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mFillScroller.isFinished()) {
                    scrollHead(dy);
                }
                updateSecondViewTop();
            }
        });
    }

    private void updateSecondViewTop() {
        View secondView = findSecondView();
        if (secondView != null && (secondView.getTop() == mMinTop || secondView.getTop() == mCloseOffset)) {
            mSecondViewTop = secondView.getTop();
        } else if (secondView == null || secondView.getTop() > mCloseOffset) {
            mSecondViewTop = Integer.MIN_VALUE;
        }
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layout) {
        super.setLayoutManager(mLayoutManager = new com.we.widget.WeRecycleView.LayoutManager(getContext()));
    }

    public void setScrollMode(int mode) {
        mScrollMode = mode;

        if (mode == SCROLL_SCALE_HEAD) {
            try {
                Field mViewFlingerField = getClass().getSuperclass().getDeclaredField("mViewFlinger");
                mViewFlingerField.setAccessible(true);
                Object mViewFlinger = mViewFlingerField.get(this);
                Field scrollerField = mViewFlinger.getClass().getDeclaredField("mScroller");
                scrollerField.setAccessible(true);
                scrollerField.set(mViewFlinger, mWeScroller = new com.we.widget.WeRecycleView.WeScroller(getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setMaxScale(float max) {
        if (max <= 1f) {
            throw new IllegalArgumentException(" maxScale must bigger then 1f");
        }
        mMaxScale = max;
    }

    /**
     * head收起时预留的空间，一般预留titlebar的高度
     *
     * @param closeOffset
     */
    public void setCloseOffset(int closeOffset) {
        mCloseOffset = closeOffset;
    }


    /**
     * 设置安全高度，
     *
     * @param safeHeight
     */
    public void setSafeHeight(int safeHeight) {
        mSafeHeight = safeHeight;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mLayoutManager != null && mLayoutManager.findFirstVisibleItemPosition() == 0) {
            mHead = getChildAt(0);
        }

        //LinearLayoutManager.scrollBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state)
        //会修正view的位置，导致我们设置的offset失效
        View secondView = findSecondView();
        if (secondView != null && mSecondViewTop != Integer.MIN_VALUE && secondView.getTop() != mSecondViewTop) {
            int dy = mSecondViewTop - secondView.getTop();
            int N = getChildCount();
            int start = indexOfChild(secondView);
            for (int i = start; i < N; i++) {
                getChildAt(i).offsetTopAndBottom(dy);
            }
            scrollHead(0);
        }

        if (mLayoutListener != null) {
            mLayoutListener.onLayout(l, t, r, b);
        }
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    private class LayoutManager extends LinearLayoutManager {

        public LayoutManager(Context context) {
            super(context);
        }


        @Override
        public int scrollVerticallyBy(final int dy, Recycler recycler, State state) {
            if (mHead == null || mScrollMode != SCROLL_SCALE_HEAD) {
                return super.scrollVerticallyBy(dy, recycler, state);
            }

            int vresult = super.scrollVerticallyBy(dy, recycler, state);
            int overscrollY = dy - vresult;

            boolean reachTop = overscrollY != 0 && dy < 0;
            boolean reachBottom = overscrollY != 0 && dy > 0;
            int firstItem = mLayoutManager.findFirstVisibleItemPosition();
            View secondView = findSecondView();

            //到顶部了
            if (reachTop && mMode == MODE_IDLE && mFillScroller.isFinished()) {
                //需要过度回弹动画
                overScroll(dy);
            }
            //到底部填充不满时，滑动子view
            else if (reachBottom && firstItem <= 1 && secondView != null) {
                int top = secondView.getTop();
                int N = com.we.widget.WeRecycleView.this.getChildCount();
                int sum = 0;
                View child;
                for (int i = 0; i < N; i++) {
                    child = com.we.widget.WeRecycleView.this.getChildAt(i);
                    if (child != mHead) {
                        sum += child.getMeasuredHeight();
                    }
                }
                int space = getMeasuredHeight() - mCloseOffset;
                boolean needFill = sum < space + mSafeHeight;
                if (sum <= space) {
                    mMinTop = mCloseOffset;
                } else if (mSafeHeight == 0) {
                    mMinTop = mCloseOffset - (sum - space);
                } else {
                    mMinTop = mCloseOffset - (sum - space);
                    mMinTop = Math.min(mMinTop, mCloseOffset - mSafeHeight);
                }

                mMinTop = Math.max(mMinTop, mCloseOffset - mSafeHeight);

                if (needFill) {
                    if (isDraging) {
                        int to = Math.max(mMinTop, top - overscrollY);
                        int trueDy = to - top;
                        for (int i = 0; i < N; i++) {
                            com.we.widget.WeRecycleView.this.getChildAt(i).offsetTopAndBottom(trueDy);
                        }
                        scrollHead(0);
                    } else if (mFillScroller.isFinished()) {
                        mFlyScroller.abortAnimation();
                        int to = dy > 0 ? mMinTop : mHead.getMeasuredHeight();
                        int delta = to - top;
                        int duration = computeDuration(Math.abs(delta), mHead.getMeasuredHeight(), 0);
                        mFillScroller.startScroll(0, top, 0, delta, duration);
                        ViewCompat.postInvalidateOnAnimation(com.we.widget.WeRecycleView.this);
                    }
                }
            }

            return vresult;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        addEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(e);
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mHead == null || mScrollMode == SCROLL_NONE) {
            return super.onTouchEvent(e);
        }
        addEvent(e);
        mActionIndex = e.getActionIndex();
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onActionDown(e);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(e);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onActionPointerDown(e);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(e);
                break;
        }
        return mMode == MODE_SCALE || mMode == MODE_FLING ? true : super.onTouchEvent(e);
    }

    private void onActionDown(MotionEvent e) {
        mScaleScroller.abortAnimation();
        mEdgeScroller.abortAnimation();
        mPointerId = e.getPointerId(0);
        mLastY = (int) (e.getY() + 0.5f);
        isDraging = false;
    }

    private void onActionPointerDown(MotionEvent e) {
        mPointerId = e.getPointerId(mActionIndex);
        mLastY = (int) (e.getY(e.findPointerIndex(mPointerId)) + 0.5f);
    }

    private void onActionMove(MotionEvent e) {
        final int index = e.findPointerIndex(mPointerId);
        if (index >= 0) {
            int y = (int) (e.getY(index) + 0.5f);
            mMode = MODE_SCALE;
            if (mLastY != 0) {
                mDy = y - mLastY;
                if (reachTop() && mDy > 0 || mHead.getScaleY() != 1f) {
                    scaleHead(mDy);
                } else if (!canScrollY()) {
                    if (mDy > 0) {
                        super.scrollBy(0, -mDy);
                    } else {
                        mMode = MODE_TOUCH;
                    }
                    isDraging = true;
                } else {
                    mMode = MODE_TOUCH;
                }
            }
            mLastY = y;
        }
    }

    private void onActionUp(MotionEvent e) {
        View secondView = findSecondView();
        if (secondView != null) {
            int firstItem = mLayoutManager.findFirstVisibleItemPosition();
            int top = secondView.getTop();
            mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
            float velocityY = mVelocityTracker.getYVelocity();
            int headHeight = mHead.getMeasuredHeight();
            //缩放
            if (top != mHead.getBottom() && firstItem <= 1 && mHead.getScaleY() != 1f) {
                mMode = MODE_SCALE;
                int delta = headHeight - top;
                int duration = computeDuration(Math.abs(delta), headHeight, velocityY);
                mScaleScroller.startScroll(0, top, 0, delta, duration);
                ViewCompat.postInvalidateOnAnimation(this);
                resetState();
            } else {
                //纠正head位置
                if (Math.abs(velocityY) < 5000 && firstItem <= 1 || !canScrollY()) {
                    mMode = MODE_FLING;
                    int to;
                    if (top < mCloseOffset) {
                        if (mDy <= 0) {
                            to = mMinTop;
                        } else {
                            to = mHead.getMeasuredHeight();
                        }
                    } else {
                        if (mDy < 0) {
                            to = Math.abs(velocityY) < 2500 ? mCloseOffset : mMinTop;
                        } else {
                            to = mHead.getMeasuredHeight();
                        }
                    }
                    if (canScrollY() && mDy <= 0 && top <= mMinTop) {
                        mMode = MODE_IDLE;
                    } else {
                        int delta = to - top;
                        int duration = computeDuration(Math.abs(delta), mHead.getMeasuredHeight(), velocityY);
                        mFlyScroller.startScroll(0, top, 0, delta, duration);
                        ViewCompat.postInvalidateOnAnimation(this);
                        resetState();
                    }
                } else {
                    mMode = MODE_IDLE;
                }
            }
        } else {
            mMode = MODE_IDLE;
        }
        mVelocityTracker.clear();
        isDraging = false;
    }


    private void addEvent(MotionEvent e) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(e);
    }


    private boolean canScrollY() {
        return getChildCount() == 0
                || getAdapter() == null
                || mLayoutManager == null
                || !(mLayoutManager.findLastVisibleItemPosition() == getAdapter().getItemCount() - 1
                && getChildAt(getChildCount() - 1).getTop() + getChildAt(getChildCount() - 1).getMeasuredHeight() < getBottom());
    }

    private void scrollHead(int dy) {
        View secondView = findSecondView();
        updateSecondViewTop();
        if (mHead == null
                || mScrollMode != SCROLL_SCALE_HEAD
                || secondView == null
                || mLayoutManager == null) {
            return;
        }

        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        if (firstItem == 0) {
            int child1Top = secondView.getTop();
            int toBottom;
            int headHeight = mHead.getMeasuredHeight();
            if (child1Top <= 0) {
                toBottom = -headHeight;
            } else {
                toBottom = (int) (child1Top +
                        (1 - child1Top * 1f / headHeight) * headHeight / 2);
            }
            toBottom = Math.max(0, Math.min(headHeight, toBottom));

            //修正最后一帧动画没执行
            if (child1Top - dy >= headHeight) {
                int trueDy = headHeight - child1Top;
                int N = getChildCount();
                for (int i = 1; i < N; i++) {
                    getChildAt(i).offsetTopAndBottom(trueDy);
                }
                mHead.offsetTopAndBottom(0 - mHead.getTop());
            } else {
                mHead.offsetTopAndBottom(toBottom - mHead.getBottom());
            }

            if (mWeScroller != null) {
                if (child1Top > 0 && child1Top < headHeight) {
                    mWeScroller.flingContinue(true, dy > 0 ? child1Top : headHeight - child1Top - mCloseOffset);
                } else {
                    mWeScroller.flingContinue(false, 0);
                }
            }
        } else if (mWeScroller != null) {
            mWeScroller.flingContinue(false, 0);
        }

        boolean enterSafe;
        if (firstItem <= 1 && secondView != null) {
            int top = secondView.getTop();
            enterSafe = top + mSafeHeight <= mCloseOffset;
        } else {
            enterSafe = false;
        }
        if (mListener != null) {
            mListener.onEnterSafeArea(enterSafe);
        }

        responseExpand();
    }

    private void overScroll(final int dy) {
        if (Math.abs(dy) < getResources().getDisplayMetrics().density * 15) {
            return;
        }
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                View secondView = findSecondView();
                if (secondView != null) {
                    int top = secondView.getTop();
                    int vy = Math.abs(dy) * 2;
                    vy = Math.max(MIN_SCALE_VY, Math.min(vy, MAX_SCALE_VY));
                    mEdgeScroller.fling(0, top, 0, vy
                            , 0, 0, 0, mHead.getMeasuredHeight(), 0, mHead.getMeasuredHeight());
                    ViewCompat.postInvalidateOnAnimation(com.we.widget.WeRecycleView.this);
                }
            }
        });
    }

    private void scaleHead(int dy) {
        View secondView = findSecondView();
        if (mHead == null || secondView == null) {
            return;
        }
        if (dy > 0) {
            float top = secondView.getTop();
            float input = Math.max(0, Math.min(1, top / (mHead.getMeasuredHeight() * mMaxScale)));
            dy = Math.round(dy * (1 - mInterpolator.getInterpolation(input)));
        }

        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            getChildAt(i).offsetTopAndBottom(dy);
        }
        int bottom = secondView.getTop();
        float height = mHead.getMeasuredHeight();
        float scale = bottom / height;
        scale = Math.max(1f, scale);
        mHead.setPivotY(0);
        mHead.setScaleX(scale);
        mHead.setScaleY(scale);
        mHead.offsetTopAndBottom(0 - mHead.getTop());

        Log.e("Chw", "top " + mHead.getTop() + " bottom " + mHead.getBottom());
    }

    private boolean reachTop() {
        if (mLayoutManager == null || getChildCount() == 0) {
            return false;
        }
        return mLayoutManager.findFirstVisibleItemPosition() == 0
                && getChildAt(0).getTop() >= 0;
    }


    private void resetState() {
        try {
            Class clz = getClass();
            while (clz != RecyclerView.class) {
                clz = clz.getSuperclass();
            }
            if (clz == RecyclerView.class) {
                Method setScrollState = clz.getDeclaredMethod("setScrollState", Integer.TYPE);
                setScrollState.setAccessible(true);
                setScrollState.invoke(this, SCROLL_STATE_IDLE);
            }

            Field field = clz.getDeclaredField("mScrollState");
            field.setAccessible(true);
            int value = (int) field.get(this);
            Log.e("Chw", "resetState " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        View secondView = findSecondView();
        if (secondView == null) {
            return;
        }

        int top = secondView.getTop();
        //松手缩放动画
        if (!mScaleScroller.isFinished() && mScaleScroller.computeScrollOffset()) {
            scaleHead(mScaleScroller.getCurrY() - top);
            ViewCompat.postInvalidateOnAnimation(this);
        }

        //过度拉伸动画
        if (!mEdgeScroller.isFinished() && mEdgeScroller.computeScrollOffset()) {
            scaleHead(mEdgeScroller.getCurrY() - top);
            ViewCompat.postInvalidateOnAnimation(this);
        }

        //up时速度过小，通过mFlyScroller来fix head的位置
        if (!mFlyScroller.isFinished() && mFlyScroller.computeScrollOffset()) {
            scrollBy(0, -mFlyScroller.getCurrY() + top);
            ViewCompat.postInvalidateOnAnimation(this);
            Log.e("Chw", "up时速度过小 " + (-mFlyScroller.getCurrY() + top) + " top "
                    + (secondView == null ? "" : secondView.getTop()));
            if (mFlyScroller.isFinished() && secondView.getTop() == mHead.getMeasuredHeight()) {
                overScroll(0);
            }
        }

        //确保head不会露半个
        if (!mFillScroller.isFinished() && mFillScroller.computeScrollOffset()) {
            int dy = mFillScroller.getCurrY() - top;
            int N = getChildCount() - 1;

            View child;
            for (int i = 0; i <= N; i++) {
                child = getChildAt(i);
                child.offsetTopAndBottom(dy);
                if (i == N) {
                    child.setBottom(getHeight());
                }
            }

            scrollHead(dy);
            ViewCompat.postInvalidateOnAnimation(this);

            if (mFillScroller.isFinished() && secondView.getTop() == mHead.getMeasuredHeight()) {
                overScroll(dy);
            }

        }
    }

    private int computeDuration(int delta, int total, float velocity) {
        int half = total / 2;
        float distanceRatio = Math.min(1f, 1.0f * Math.abs(delta) / total);
        int maxDuration = 400;
        distanceRatio -= 0.5f;
        distanceRatio *= 0.3f * (float) Math.PI / 2.0f;
        final float distance = half + half * (float) Math.sin(distanceRatio);
        int duration = 0;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(800 * Math.abs(distance / velocity));
        }
        if (duration > 1000) {
            duration = (int) ((Math.abs(delta) * 1f / total) * maxDuration);
        }
        int dur = Math.max(250, Math.min(duration, maxDuration));
        return dur;
    }

    private void responseExpand() {
        if (mListener == null || getChildCount() < 2 || mLayoutManager == null) {
            return;
        }

        float percent;
        View secondView = findSecondView();
        if (mLayoutManager.findFirstVisibleItemPosition() != 0 || secondView == null) {
            percent = 0;
        } else {
            int total = mHead.getMeasuredHeight() - mCloseOffset;
            int cur = secondView.getTop() - mCloseOffset;
            percent = Math.max(0, Math.min(1, cur * 1f / total));
        }

        if (percent != mExpandPercent) {
            mListener.onExpand(mExpandPercent = percent);
        }
    }

    public void setOnHeadExpandListener(com.we.widget.WeRecycleView.OnHeadExpandListener l) {
        mListener = l;
    }


    public void setOnLayoutListener(com.we.widget.WeRecycleView.OnLayoutListener l) {
        mLayoutListener = l;
    }

    public interface OnHeadExpandListener {
        void onExpand(float percent);

        void onEnterSafeArea(boolean isEnter);
    }

    public interface OnLayoutListener {
        void onLayout(int l, int t, int r, int b);
    }

    private View findSecondView() {
        View view;
        for (int i = 0; i <= 1; i++) {
            view = getChildAt(i);
            if (view != null
                    && view.getLayoutParams() != null
                    && mLayoutManager.getPosition(view) == 1) {
                return view;
            }
        }
        return null;
    }


    private static class WeScroller extends OverScroller {
        private int mMode;

        private final com.we.widget.WeRecycleView.WeScroller.WeSplineOverScroller mWeScrollerY;
        private Object mSuperScrollerY;
        private Interpolator mInterpolator;

        private final boolean mFlywheel;

        private static final int DEFAULT_DURATION = 250;
        private static final int SCROLL_MODE = 0;
        private static final int FLING_MODE = 1;

        private boolean mFlingContinue = false;
        private static int DEFAULT_DY;


        public WeScroller(Context context) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
            this(context, null);
        }

        public WeScroller(Context context, Interpolator interpolator) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
            this(context, interpolator, true);
        }


        public WeScroller(Context context, Interpolator interpolator, boolean flywheel) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, ClassNotFoundException {
            super(context, interpolator);
            if (interpolator == null) {
                mInterpolator = new com.we.widget.WeRecycleView.WeScroller.ViscousFluidInterpolator();
            } else {
                mInterpolator = interpolator;
            }
            mFlywheel = flywheel;
            mWeScrollerY = new com.we.widget.WeRecycleView.WeScroller.WeSplineOverScroller(context, this, true);
            mSuperScrollerY = getFieldValue(this, "mScrollerY");

            DEFAULT_DY = (int) (context.getResources().getDisplayMetrics().density * 10);

            checkSuper();
        }


        public static Object getFieldValue(Object paramClass, String paramString) {
            if (paramClass == null) {
                return null;
            }
            Field field = null;
            Object object = null;
            Class cl = paramClass.getClass();
            for (; field == null && cl != null; ) {
                try {
                    field = cl.getDeclaredField(paramString);
                    if (field != null) {
                        field.setAccessible(true);
                    }
                } catch (Exception e) {

                }
                if (field == null) {
                    cl = cl.getSuperclass();
                }
            }
            try {
                if (field != null) {
                    object = field.get(paramClass);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return object;
        }

        public static void setFieldValue(Object object, String fieldName, Object fieldValue) {
            if (object == null || TextUtils.isEmpty(fieldName)) {
                return;
            }
            Field field = null;
            Class cl = object.getClass();
            for (; field == null && cl != null; ) {
                try {
                    field = cl.getDeclaredField(fieldName);
                    if (field != null) {
                        field.setAccessible(true);
                    }
                } catch (Throwable e) {

                }
                if (field == null) {
                    cl = cl.getSuperclass();
                }
            }
            if (field != null) {
                try {
                    field.set(object, fieldValue);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public boolean computeScrollOffset() {
            if (isFinished()) {
                return false;
            }

            switch (mMode) {
                case SCROLL_MODE:
                    long time = AnimationUtils.currentAnimationTimeMillis();
                    // Any scroller can be used for time, since they were started
                    // together in scroll mode. We use X here.
                    final long elapsedTime = time - mWeScrollerY.mStartTime;

                    final int duration = mWeScrollerY.mDuration;
                    if (elapsedTime < duration) {
                        final float q = mInterpolator.getInterpolation(elapsedTime / (float) duration);
                        mWeScrollerY.updateScroll(q);
                    } else {
                        abortAnimation();
                    }
                    break;

                case FLING_MODE:
                    if (!mWeScrollerY.mFinished) {
                        if (!mWeScrollerY.update()) {
                            if (!mWeScrollerY.continueWhenFinished() && !mFlingContinue) {
                                mWeScrollerY.finish();
                            }
                        }
                    }

                    break;
            }
            mWeScrollerY.updateSuper();
            return true;
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            startScroll(startX, startY, dx, dy, DEFAULT_DURATION);
        }


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            mMode = SCROLL_MODE;
            mWeScrollerY.startScroll(startY, dy, duration);
        }


        @Override
        public boolean springBack(int startX, int startY, int minX, int maxX, int minY, int maxY) {
            mMode = FLING_MODE;
            final boolean spingbackY = mWeScrollerY.springback(startY, minY, maxY);
            return spingbackY;
        }

        @Override
        public void fling(int startX, int startY, int velocityX, int velocityY,
                          int minX, int maxX, int minY, int maxY) {
            fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
        }


        @Override
        public void fling(int startX, int startY, int velocityX, int velocityY,
                          int minX, int maxX, int minY, int maxY, int overX, int overY) {
            if (mFlywheel && !isFinished()) {
                float oldVelocityY = mWeScrollerY.mCurrVelocity;
                if (Math.signum(velocityX) == Math.signum(0) &&
                        Math.signum(velocityY) == Math.signum(oldVelocityY)) {
                    velocityY += oldVelocityY;
                }
            }

            mMode = FLING_MODE;
            mWeScrollerY.fling(startY, velocityY, minY, maxY, overY);
        }


        @Override
        public void notifyVerticalEdgeReached(int startY, int finalY, int overY) {
            mWeScrollerY.notifyEdgeReached(startY, finalY, overY);
        }


        @Override
        public boolean isOverScrolled() {
            return (!mWeScrollerY.mFinished && mWeScrollerY.mState != com.we.widget.WeRecycleView.WeScroller.WeSplineOverScroller.SPLINE);
        }

        @Override
        public void abortAnimation() {
            mWeScrollerY.finish();
        }


        static class WeSplineOverScroller {
            private int mStart;

            private int mCurrentPosition;

            private int mDy;

            private boolean mEnableHeadAnim;
            private boolean hasUseHeadAnim;

            private int mFinal;

            private int mVelocity;

            private float mCurrVelocity;

            private float mDeceleration;

            private long mStartTime;

            private int mDuration;

            private int mSplineDuration;

            private int mSplineDistance;

            private boolean mFinished;

            private int mOver;

            private float mFlingFriction = ViewConfiguration.getScrollFriction();

            private int mState = SPLINE;

            private static final float GRAVITY = 2000.0f;

            private float mPhysicalCoeff;

            private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
            private static final float INFLEXION = 0.35f;
            private static final float START_TENSION = 0.5f;
            private static final float END_TENSION = 1.0f;
            private static final float P1 = START_TENSION * INFLEXION;
            private static final float P2 = 1.0f - END_TENSION * (1.0f - INFLEXION);

            private static final int NB_SAMPLES = 100;
            private static final float[] SPLINE_POSITION = new float[NB_SAMPLES + 1];
            private static final float[] SPLINE_TIME = new float[NB_SAMPLES + 1];

            private static final int SPLINE = 0;
            private static final int CUBIC = 1;
            private static final int BALLISTIC = 2;

            static {
                float x_min = 0.0f;
                float y_min = 0.0f;
                for (int i = 0; i < NB_SAMPLES; i++) {
                    final float alpha = (float) i / NB_SAMPLES;

                    float x_max = 1.0f;
                    float x, tx, coef;
                    while (true) {
                        x = x_min + (x_max - x_min) / 2.0f;
                        coef = 3.0f * x * (1.0f - x);
                        tx = coef * ((1.0f - x) * P1 + x * P2) + x * x * x;
                        if (Math.abs(tx - alpha) < 1E-5) {
                            break;
                        }
                        if (tx > alpha) {
                            x_max = x;
                        } else {
                            x_min = x;
                        }
                    }
                    SPLINE_POSITION[i] = coef * ((1.0f - x) * START_TENSION + x) + x * x * x;

                    float y_max = 1.0f;
                    float y, dy;
                    while (true) {
                        y = y_min + (y_max - y_min) / 2.0f;
                        coef = 3.0f * y * (1.0f - y);
                        dy = coef * ((1.0f - y) * START_TENSION + y) + y * y * y;
                        if (Math.abs(dy - alpha) < 1E-5) {
                            break;
                        }
                        if (dy > alpha) {
                            y_max = y;
                        } else {
                            y_min = y;
                        }
                    }
                    SPLINE_TIME[i] = coef * ((1.0f - y) * P1 + y * P2) + y * y * y;
                }
                SPLINE_POSITION[NB_SAMPLES] = SPLINE_TIME[NB_SAMPLES] = 1.0f;
            }


            private WeakReference<com.we.widget.WeRecycleView.WeScroller> mWeScroller;
            private boolean mIsY;

            WeSplineOverScroller(Context context, com.we.widget.WeRecycleView.WeScroller scroller, boolean isY) {
                mFinished = true;
                final float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
                mPhysicalCoeff = SensorManager.GRAVITY_EARTH
                        * 39.37f // inch/meter
                        * ppi
                        * 0.84f; // look and feel tuning

                mWeScroller = new WeakReference<>(scroller);
                mIsY = isY;
            }

            void updateScroll(float q) {
                mCurrentPosition = mStart + Math.round(q * (mFinal - mStart));
            }


            static private float getDeceleration(int velocity) {
                return velocity > 0 ? -GRAVITY : GRAVITY;
            }


            private void adjustDuration(int start, int oldFinal, int newFinal) {
                final int oldDistance = oldFinal - start;
                final int newDistance = newFinal - start;
                final float x = Math.abs((float) newDistance / oldDistance);
                final int index = (int) (NB_SAMPLES * x);
                if (index < NB_SAMPLES) {
                    final float x_inf = (float) index / NB_SAMPLES;
                    final float x_sup = (float) (index + 1) / NB_SAMPLES;
                    final float t_inf = SPLINE_TIME[index];
                    final float t_sup = SPLINE_TIME[index + 1];
                    final float timeCoef = t_inf + (x - x_inf) / (x_sup - x_inf) * (t_sup - t_inf);
                    mDuration *= timeCoef;
                }
            }

            void startScroll(int start, int distance, int duration) {
                mFinished = false;

                mCurrentPosition = mStart = start;
                mFinal = start + distance;

                mStartTime = AnimationUtils.currentAnimationTimeMillis();
                mDuration = duration;

                // Unused
                mDeceleration = 0.0f;
                mVelocity = 0;
            }

            void finish() {

                mFinished = true;

                mDy = 0;
                mEnableHeadAnim = false;
            }

            boolean springback(int start, int min, int max) {
                mFinished = true;

                mCurrentPosition = mStart = mFinal = start;
                mVelocity = 0;

                mStartTime = AnimationUtils.currentAnimationTimeMillis();
                mDuration = 0;

                if (start < min) {
                    startSpringback(start, min, 0);
                } else if (start > max) {
                    startSpringback(start, max, 0);
                }

                return !mFinished;
            }

            private void startSpringback(int start, int end, int velocity) {
                mFinished = false;
                mState = CUBIC;
                mCurrentPosition = mStart = start;
                mFinal = end;
                final int delta = start - end;
                mDeceleration = getDeceleration(delta);
                mVelocity = -delta;
                mOver = Math.abs(delta);
                mDuration = (int) (300.0 * Math.sqrt(-2.0 * delta / mDeceleration));
            }

            void fling(int start, int velocity, int min, int max, int over) {
                mOver = over;
                mFinished = false;
                hasUseHeadAnim = false;
                mDy = 0;
                mEnableHeadAnim = false;
                mCurrVelocity = mVelocity = velocity;
                mDuration = mSplineDuration = 0;
                mStartTime = AnimationUtils.currentAnimationTimeMillis();
                mCurrentPosition = mStart = start;

                if (start > max || start < min) {
                    startAfterEdge(start, min, max, velocity);
                    return;
                }

                mState = SPLINE;
                double totalDistance = 0.0;

                if (velocity != 0) {
                    mDuration = mSplineDuration = getSplineFlingDuration(velocity);
                    totalDistance = getSplineFlingDistance(velocity);
                }

                mSplineDistance = (int) (totalDistance * Math.signum(velocity));

                mFinal = start + mSplineDistance;

                // Clamp to a valid final position
                if (mFinal < min) {
                    adjustDuration(mStart, mFinal, min);
                    mFinal = min;
                }

                if (mFinal > max) {
                    adjustDuration(mStart, mFinal, max);
                    mFinal = max;
                }
                updateSuper();
            }

            private double getSplineDeceleration(int velocity) {
                return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
            }

            private double getSplineFlingDistance(int velocity) {
                final double l = getSplineDeceleration(velocity);
                final double decelMinusOne = DECELERATION_RATE - 1.0;
                return mFlingFriction * mPhysicalCoeff * Math.exp(DECELERATION_RATE / decelMinusOne * l);
            }


            private int getSplineFlingDuration(int velocity) {
                final double l = getSplineDeceleration(velocity);
                final double decelMinusOne = DECELERATION_RATE - 1.0;
                return (int) (1000f * Math.exp(l / decelMinusOne));
            }

            private void fitOnBounceCurve(int start, int end, int velocity) {
                // Simulate a bounce that started from edge
                final float durationToApex = -velocity / mDeceleration;
                // The float cast below is necessary to avoid integer overflow.
                final float velocitySquared = (float) velocity * velocity;
                final float distanceToApex = velocitySquared / 2.0f / Math.abs(mDeceleration);
                final float distanceToEdge = Math.abs(end - start);
                final float totalDuration = (float) Math.sqrt(
                        2.0 * (distanceToApex + distanceToEdge) / Math.abs(mDeceleration));
                mStartTime -= (int) (1000f * (totalDuration - durationToApex));
                mCurrentPosition = mStart = end;
                mVelocity = (int) (-mDeceleration * totalDuration);
            }

            private void startBounceAfterEdge(int start, int end, int velocity) {
                mDeceleration = getDeceleration(velocity == 0 ? start - end : velocity);
                fitOnBounceCurve(start, end, velocity);
                onEdgeReached();
            }

            private void startAfterEdge(int start, int min, int max, int velocity) {
                if (start > min && start < max) {
                    mFinished = true;
                    return;
                }
                final boolean positive = start > max;
                final int edge = positive ? max : min;
                final int overDistance = start - edge;
                boolean keepIncreasing = overDistance * velocity >= 0;
                if (keepIncreasing) {
                    // Will result in a bounce or a to_boundary depending on velocity.
                    startBounceAfterEdge(start, edge, velocity);
                } else {
                    final double totalDistance = getSplineFlingDistance(velocity);
                    if (totalDistance > Math.abs(overDistance)) {
                        fling(start, velocity, positive ? min : start, positive ? start : max, mOver);
                    } else {
                        startSpringback(start, edge, velocity);
                    }
                }
            }

            void notifyEdgeReached(int start, int end, int over) {
                if (mState == SPLINE) {
                    mOver = over;
                    mStartTime = AnimationUtils.currentAnimationTimeMillis();
                    startAfterEdge(start, end, end, (int) mCurrVelocity);
                }
            }

            private void onEdgeReached() {
                final float velocitySquared = (float) mVelocity * mVelocity;
                float distance = velocitySquared / (2.0f * Math.abs(mDeceleration));
                final float sign = Math.signum(mVelocity);

                if (distance > mOver) {
                    mDeceleration = -sign * velocitySquared / (2.0f * mOver);
                    distance = mOver;
                }

                mOver = (int) distance;
                mState = BALLISTIC;
                mFinal = mStart + (int) (mVelocity > 0 ? distance : -distance);
                mDuration = -(int) (1000f * mVelocity / mDeceleration);
            }

            boolean continueWhenFinished() {
                switch (mState) {
                    case SPLINE:
                        if (mDuration < mSplineDuration) {
                            mCurrentPosition = mStart = mFinal;
                            mVelocity = (int) mCurrVelocity;
                            mDeceleration = getDeceleration(mVelocity);
                            mStartTime += mDuration;
                            onEdgeReached();
                        } else {
                            return false;
                        }
                        break;
                    case BALLISTIC:
                        mStartTime += mDuration;
                        startSpringback(mFinal, mStart, 0);
                        break;
                    case CUBIC:
                        return false;
                }

                update();
                return true;
            }


            boolean update() {
                final long time = AnimationUtils.currentAnimationTimeMillis();
                final long currentTime = time - mStartTime;

                if (currentTime == 0) {
                    return mDuration > 0;
                }
                if (currentTime > mDuration && !mWeScroller.get().mFlingContinue) {
                    return false;
                }

                double distance = 0.0;
                switch (mState) {
                    case SPLINE: {
                        final float t = (float) currentTime / mSplineDuration;
                        final int index = (int) (NB_SAMPLES * t);
                        float distanceCoef = 1.f;
                        float velocityCoef = 0.f;
                        if (index < NB_SAMPLES) {
                            final float t_inf = (float) index / NB_SAMPLES;
                            final float t_sup = (float) (index + 1) / NB_SAMPLES;
                            final float d_inf = SPLINE_POSITION[index];
                            final float d_sup = SPLINE_POSITION[index + 1];
                            velocityCoef = (d_sup - d_inf) / (t_sup - t_inf);
                            distanceCoef = d_inf + (t - t_inf) * velocityCoef;
                        }

                        distance = distanceCoef * mSplineDistance;
                        if (currentTime < mDuration) {
                            mCurrVelocity = velocityCoef * mSplineDistance / mSplineDuration * 1000f;
                        }
                        break;
                    }

                    case BALLISTIC: {
                        final float t = currentTime / 1000f;
                        mCurrVelocity = mVelocity + mDeceleration * t;
                        distance = mVelocity * t + mDeceleration * t * t / 2.0f;
                        break;
                    }

                    case CUBIC: {
                        final float t = (float) (currentTime) / mDuration;
                        final float t2 = t * t;
                        final float sign = Math.signum(mVelocity);
                        distance = sign * mOver * (3.0f * t2 - 2.0f * t * t2);
                        mCurrVelocity = sign * mOver * 6.0f * (-t + t2);
                        break;
                    }
                }
                int dy = mStart + (int) Math.round(distance) - mCurrentPosition;
                if (mDy == 0 && dy != 0) {
                    mDy = dy;
                }

                if (mWeScroller.get() != null && mWeScroller.get().mFlingContinue || hasUseHeadAnim) {
                    if (mEnableHeadAnim) {
                        if (Math.abs(mDy) < DEFAULT_DY) {
                            mDy = mDy / Math.abs(mDy) * DEFAULT_DY;
                        }
                        mCurrentPosition += mDy;
                        hasUseHeadAnim = true;
                    } else {
                        mCurrentPosition = mStart + (int) Math.round(distance);
                    }
                } else {
                    mCurrentPosition = mStart + (int) Math.round(distance);
                }


                return true;
            }

            private void updateSuper() {
                if (mWeScroller == null || mWeScroller.get() == null) {
                    return;
                }
                setFieldValue(mWeScroller.get().mSuperScrollerY, "mCurrentPosition", mCurrentPosition);
                setFieldValue(mWeScroller.get().mSuperScrollerY, "mFinished", mFinished);
                setFieldValue(mWeScroller.get().mSuperScrollerY, "mFinal", mFinal);
            }


        }

        static class ViscousFluidInterpolator implements Interpolator {
            private static final float VISCOUS_FLUID_SCALE = 8.0f;

            private static final float VISCOUS_FLUID_NORMALIZE;
            private static final float VISCOUS_FLUID_OFFSET;

            static {
                VISCOUS_FLUID_NORMALIZE = 1.0f / viscousFluid(1.0f);
                VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * viscousFluid(1.0f);
            }

            private static float viscousFluid(float x) {
                x *= VISCOUS_FLUID_SCALE;
                if (x < 1.0f) {
                    x -= (1.0f - (float) Math.exp(-x));
                } else {
                    float start = 0.36787944117f;
                    x = 1.0f - (float) Math.exp(1.0f - x);
                    x = start + x * (1.0f - start);
                }
                return x;
            }

            @Override
            public float getInterpolation(float input) {
                final float interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input);
                if (interpolated > 0) {
                    return interpolated + VISCOUS_FLUID_OFFSET;
                }
                return interpolated;
            }


        }

        public void flingContinue(boolean flag, int less) {
            mFlingContinue = flag;
            if (!mWeScrollerY.mEnableHeadAnim) {
                mWeScrollerY.mEnableHeadAnim = (mWeScrollerY.mFinal - mWeScrollerY.mCurrentPosition) < less;
            }

            if (mFlingContinue && mWeScrollerY.mEnableHeadAnim) {
                mWeScrollerY.mFinal = mWeScrollerY.mCurrentPosition + less;
                mWeScrollerY.updateSuper();
            }
        }

        public void checkSuper() throws NoSuchFieldException {
            mSuperScrollerY.getClass().getDeclaredField("mCurrentPosition");
            mSuperScrollerY.getClass().getDeclaredField("mFinished");
            mSuperScrollerY.getClass().getDeclaredField("mFinal");
        }

    }

}