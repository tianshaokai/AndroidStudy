package com.tianshaokai.canvasdemo.largeImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 大图操作控件
 * Created by zhy on 15/5/16.
 * @author alafighting 2016-03 重新实现，更好的处理移动\涂画\擦除\撤销\保存功能
 */
public class LargeImageView extends FrameLayout {

    private ImageView mBgView;
    private View mDrawView;

    private Paint mPaint;
    private Paint mPaintEraser;
    private List<PathItem> paths = new ArrayList<>();

    public static class PathItem {
        Path path;
        Paint paint;
        PathItem(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }

    /**
     * 图片的宽度和高度
     */
    private int mImageWidth, mImageHeight;
    private int mShowWidth, mShowHeight;
    /**
     * 绘制的区域
     */
    private volatile Rect mCurrentRect;

    private boolean isMove = false;
    private boolean isEraser = false;

    private Bitmap tempBitmap;

    /**
     * 判断是否已设置地图
     */
    private boolean hasBackground = false;


    public void clear() {
    /*    if(path != null) {
            path.reset();
        }
        if(pathEraser != null) {
            pathEraser.reset();
        }*/
        invalidate();
    }

    public LargeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mBgView = new ImageView(context);
        addView(mBgView);

        mDrawView = new View(context) {
            @Override
            public void draw(Canvas canvas) {
                super.draw(canvas);
                if (!hasBackground) {
                    return;
                }

                // 减小内存占用
                if (tempBitmap == null) {
                    tempBitmap = Bitmap.createBitmap(mImageWidth/2, mImageHeight/2, Bitmap.Config.ARGB_4444);
                }
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.save();
                tempCanvas.scale(0.5F, 0.5F);
                for (PathItem item : paths) {
                    tempCanvas.drawPath(item.path, item.paint);
                }
                tempCanvas.restore();
                canvas.drawBitmap(tempBitmap, new Rect(0, 0, mImageWidth/2, mImageHeight/2), new Rect(0, 0, mImageWidth, mImageHeight), null);
            }
        };
        addView(mDrawView);

        mDrawView.setOnTouchListener(new OnTouchListener() {
            private Path path;
            private float downX;
            private float downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!hasBackground) {
                    return false;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;
                }

                if(isMove) {
                    touchMove(event);
                } else {
                    touchDraw(event);
                }
                return true;
            }

            /**
             * 触摸移动
             * @param event
             */
            void touchMove(MotionEvent event) {
                float currentX = event.getX();
                float currentY = event.getY();

                int moveX = (int) (currentX - downX);
                int moveY = (int) (currentY - downY);

                mCurrentRect.offset(moveX, moveY);
                checkCurrentRect();

                onCurrentRectChange(new Rect(mCurrentRect));
            }
            /**
             * 触摸涂画
             * @param event
             */
            void touchDraw(MotionEvent event) {
                float touchX = event.getX();
                float touchY = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        path = new Path();
                        paths.add(new PathItem(path, isEraser ? mPaintEraser : mPaint));

                        path.moveTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        path.lineTo(touchX, touchY);
                        break;
                }
                mDrawView.postInvalidate();
            }
        });


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#FF0000"));
        mPaint.setStrokeWidth(10);

        mPaintEraser = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintEraser.setAlpha(0xFF);
        mPaintEraser.setColor(Color.TRANSPARENT);
        mPaintEraser.setStyle(Paint.Style.STROKE);

        mPaintEraser.setStrokeWidth(30);
        mPaintEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));   //设置画笔的痕迹是透明的，从而可以看到背景图片

    }

    /**
     * 设置底图
     * @param is 图片数据流
     */
    public void setImageInputStream(InputStream is) {
        // 释放当前资源
        recycle();

        if (is == null) {
            hasBackground = false;

            changeSize(mBgView, 0, 0);
            changeSize(mDrawView, 0, 0);
        } else {
            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            // Grab the bounds for the scene dimensions
            tmpOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, tmpOptions);
            mImageWidth = tmpOptions.outWidth;
            mImageHeight = tmpOptions.outHeight;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 2; // 缩放比例，可根据屏幕宽高调整
            mBgView.setImageDrawable(new BitmapDrawable(BitmapFactory.decodeStream(is, null, options)));
            mBgView.setBackgroundColor(Color.BLUE);
            mBgView.setScaleType(ImageView.ScaleType.FIT_XY);

            changeSize(mBgView, mImageWidth, mImageHeight);
            changeSize(mDrawView, mImageWidth, mImageHeight);
            hasBackground = true;
        }

        invalidate();
    }

    private void changeSize(View view, int width, int height) {
        view.setMinimumWidth(width);
        view.setMinimumHeight(height);

        FrameLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;

        params.leftMargin = 0;
        params.topMargin = 0;
        params.leftMargin = 0;
        params.topMargin = 0;
    }

    /**
     * 切换移动状态
     * @return 是否移动状态
     */
    public boolean toogleIsMove() {
        this.isMove = !isMove;
        this.isEraser = false;
        return isMove;
    }

    /**
     * 切换橡皮擦模式
     * @return 是否橡皮擦模式
     */
    public boolean toogleEraser() {
        if (isMove) {
            return false;
        }
        isEraser = !isEraser;
        return isEraser;
    }

    /**
     * 撤销
     * @return
     */
    public PathItem undo() {
        // TODO 待修复
        if (paths.isEmpty()) {
            return null;
        }

        PathItem item = paths.remove(paths.size() -1);
        mDrawView.postInvalidate();
        return item;
    }

    /**
     * 保存成图片
     * @param file
     * @param scale 比例（1表示等比，0.5表示缩小一半）
     */
    public void savePicture(File file, float scale) {
        Bitmap resultBit = Bitmap.createBitmap((int)(mImageWidth/2*scale), (int)(mImageHeight/2*scale), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(resultBit);
        canvas.save();
        canvas.scale(scale, scale);
        canvas.drawBitmap(((BitmapDrawable)mBgView.getDrawable()).getBitmap(), 0, 0, null);
        canvas.drawBitmap(tempBitmap, 0, 0, null);
        canvas.restore();
        saveBitmap(resultBit, file);
    }

    /**
     * 保存Bitmap图片到指定文件
     *
     * @param bitmap
     * @param file
     */
    private static void saveBitmap(Bitmap bitmap, File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e("saveBitmap", "保存图片异常", e);
        }
    }


    /**
     * 检查可视区域是否合法
     */
    private void checkCurrentRect() {
        // width
        if (mCurrentRect.width() < mImageWidth) {
            if (mCurrentRect.right > mImageWidth) {
                mCurrentRect.right = mImageWidth;
                mCurrentRect.left = mImageWidth - mShowWidth;
            }
            if (mCurrentRect.left < 0) {
                mCurrentRect.left = 0;
                mCurrentRect.right = mShowWidth;
            }
        } else {
            // 宽度不够
            mCurrentRect.left = (mImageWidth - mShowWidth) / 2;
            mCurrentRect.right = mCurrentRect.left + mShowWidth;
        }

        // height
        if (mCurrentRect.height() < mImageHeight) {
            if (mCurrentRect.bottom > mImageHeight) {
                mCurrentRect.bottom = mImageHeight;
                mCurrentRect.top = mImageHeight - mShowHeight;
            }
            if (mCurrentRect.top < 0) {
                mCurrentRect.top = 0;
                mCurrentRect.bottom = mShowHeight;
            }
        } else {
            // 高度不够
            mCurrentRect.top = (mImageHeight - mShowHeight) / 2;
            mCurrentRect.bottom = mCurrentRect.top + mShowHeight;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化可视区域
        if (mCurrentRect == null) {
            mShowWidth = right - left;
            mShowHeight = bottom - top;

            mCurrentRect = new Rect();
            mCurrentRect.left = (mImageWidth - mShowWidth) / 2;
            mCurrentRect.top = (mImageHeight - mShowHeight) / 2;
            mCurrentRect.right = mCurrentRect.left + mShowWidth;
            mCurrentRect.bottom = mCurrentRect.top + mShowHeight;

            onCurrentRectChange(new Rect(mCurrentRect));
        }
    }

    /**
     * 可视区域变化
     * @param rect 可视区域坐标
     */
    private void onCurrentRectChange(Rect rect) {
        FrameLayout.LayoutParams paramsBg = (LayoutParams) mBgView.getLayoutParams();
        paramsBg.leftMargin = -rect.left;
        paramsBg.topMargin = -rect.top;
        paramsBg.leftMargin = rect.right-mImageWidth;
        paramsBg.topMargin = rect.bottom-mImageHeight;

        FrameLayout.LayoutParams paramsDraw = (LayoutParams) mDrawView.getLayoutParams();
        paramsDraw.leftMargin = -rect.left;
        paramsDraw.topMargin = -rect.top;
        paramsDraw.leftMargin = rect.right-mImageWidth;
        paramsDraw.topMargin = rect.bottom-mImageHeight;

        requestLayout();
    }

    /**
     * 回收资源
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        recycle();
    }

    /**
     * 释放资源
     */
    public void recycle() {
        mCurrentRect = null;
       // paths.clear();
        if (tempBitmap != null) {
            tempBitmap.recycle();
            tempBitmap = null;
        }
    }

}
