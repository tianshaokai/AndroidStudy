package com.tianshaokai.canvasdemo.pagecurl.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 曲线View
 *
 * @author AigeStudio {@link http://blog.csdn.net/aigestudio}
 * @version 1.0.0
 * @since 2014/12/28
 */
public class CurveView extends View {
	private static final float CURVATURE = 1 / 4F;// 曲度值
	private static final float VALUE_ADDED = 1 / 400F;// 精度附加值占比
	private static final float BUFF_AREA = 1 / 50F;// 底部缓冲区域占比
	private static final float AUTO_AREA_BUTTOM_RIGHT = 3 / 4F, AUTO_AREA_BUTTOM_LEFT = 1 / 8F;// 右下角和左侧自滑区域占比
	private static final float AUTO_SLIDE_BL_V = 1 / 25F, AUTO_SLIDE_BR_V = 1 / 100F;// 滑动速度占比
	private static final float TEXT_SIZE_NORMAL = 1 / 40F, TEXT_SIZE_LARGER = 1 / 20F;// 标准文字尺寸和大号文字尺寸的占比

	private List<Bitmap> mBitmaps;// 位图数据列表

	private SlideHandler mSlideHandler;// 滑动处理Handler
	private Paint mPaint;// 画笔
	private TextPaint mTextPaint;// 文本画笔
	private Context mContext;// 上下文环境引用

	private Path mPath;// 折叠路径
	private Path mPathFoldAndNext;// 一个包含折叠和下一页区域的Path
	private Path mPathSemicircleBtm, mPathSemicircleLeft;// 底部和左边月半圆Path
	private Path mPathTrap;// 梯形区域Path

	private Region mRegionShortSize;// 短边的有效区域
	private Region mRegionCurrent;// 当前页区域，其实就是控件的大小
	private Region mRegionNext;// 当前页区域，其实就是控件的大小
	private Region mRegionFold;// 当前页区域，其实就是控件的大小
	private Region mRegionSemicircle;// 两月半圆区域

	private int mViewWidth, mViewHeight;// 控件宽高
	private int mPageIndex;// 当前显示mBitmaps数据的下标

	private float mPointX, mPointY;// 手指触摸点的坐标
	private float mValueAdded;// 精度附减值
	private float mBuffArea;// 底部缓冲区域
	private float mAutoAreaButtom, mAutoAreaRight, mAutoAreaLeft;// 右下角和左侧自滑区域
	private float mStart_X, mStart_Y;// 直线起点坐标
	private float mAutoSlideV_BL, mAutoSlideV_BR;// 滑动速度
	private float mTextSizeNormal, mTextSizeLarger;// 标准文字尺寸和大号文字尺寸
	private float mDegrees;// 当前Y边长与Y轴的夹角

	private boolean isSlide, isLastPage, isNextPage;// 是否执行滑动、是否已到最后一页、是否可显示下一页的标识值

	private Slide mSlide;// 定义当前滑动是往左下滑还是右下滑

	/**
	 * 枚举类定义滑动方向
	 */
	private enum Slide {
		LEFT_BOTTOM, RIGHT_BOTTOM
	}

	private Ratio mRatio;// 定义当前折叠边长

	/**
	 * 枚举类定义长边短边
	 */
	private enum Ratio {
		LONG, SHORT
	}

	public CurveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		/*
		 * 实例化文本画笔并设置参数
		 */
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
		mTextPaint.setTextAlign(Paint.Align.CENTER);

		/*
		 * 实例化画笔对象并设置参数
		 */
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(2);

		/*
		 * 实例化路径对象
		 */
		mPath = new Path();
		mPathFoldAndNext = new Path();
		mPathSemicircleBtm = new Path();
		mPathSemicircleLeft = new Path();
		mPathTrap = new Path();

		/*
		 * 实例化区域对象
		 */
		mRegionShortSize = new Region();
		mRegionCurrent = new Region();
		mRegionSemicircle = new Region();

		// 实例化滑动Handler处理器
		mSlideHandler = new SlideHandler();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		/*
		 * 获取控件宽高
		 */
		mViewWidth = w;
		mViewHeight = h;

		// 初始化位图数据
		if (null != mBitmaps) {
			initBitmaps();
		}

		// 计算文字尺寸
		mTextSizeNormal = TEXT_SIZE_NORMAL * mViewHeight;
		mTextSizeLarger = TEXT_SIZE_LARGER * mViewHeight;

		// 计算精度附加值
		mValueAdded = mViewHeight * VALUE_ADDED;

		// 计算底部缓冲区域
		mBuffArea = mViewHeight * BUFF_AREA;

		/*
		 * 计算自滑位置
		 */
		mAutoAreaButtom = mViewHeight * AUTO_AREA_BUTTOM_RIGHT;
		mAutoAreaRight = mViewWidth * AUTO_AREA_BUTTOM_RIGHT;
		mAutoAreaLeft = mViewWidth * AUTO_AREA_BUTTOM_LEFT;

		// 计算短边的有效区域
		computeShortSizeRegion();

		/*
		 * 计算滑动速度
		 */
		mAutoSlideV_BL = mViewWidth * AUTO_SLIDE_BL_V;
		mAutoSlideV_BR = mViewWidth * AUTO_SLIDE_BR_V;

		// 计算当前页区域
		mRegionCurrent.set(0, 0, mViewWidth, mViewHeight);
	}

	/**
	 * 初始化位图数据
	 * 缩放位图尺寸与屏幕匹配
	 */
	private void initBitmaps() {
		List<Bitmap> temp = new ArrayList<Bitmap>();
		for (int i = mBitmaps.size() - 1; i >= 0; i--) {
			Bitmap bitmap = Bitmap.createScaledBitmap(mBitmaps.get(i), mViewWidth, mViewHeight, true);
			temp.add(bitmap);
		}
		mBitmaps = temp;
	}

	/**
	 * 计算短边的有效区域
	 */
	private void computeShortSizeRegion() {
		// 短边圆形路径对象
		Path pathShortSize = new Path();

		// 用来装载Path边界值的RectF对象
		RectF rectShortSize = new RectF();

		// 添加圆形到Path
		pathShortSize.addCircle(0, mViewHeight, mViewWidth, Path.Direction.CCW);

		// 计算边界
		pathShortSize.computeBounds(rectShortSize, true);

		// 将Path转化为Region
		mRegionShortSize.setPath(pathShortSize, new Region((int) rectShortSize.left, (int) rectShortSize.top, (int) rectShortSize.right, (int) rectShortSize.bottom));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		/*
		 * 如果数据为空则显示默认提示文本
		 */
		if (null == mBitmaps || mBitmaps.size() == 0) {
			defaultDisplay(canvas);
			return;
		}

		// 重绘时重置路径
		mPath.reset();
		mPathFoldAndNext.reset();
		mPathTrap.reset();
		mPathSemicircleBtm.reset();
		mPathSemicircleLeft.reset();

		// 绘制底色
		canvas.drawColor(Color.WHITE);

		/*
		 * 如果坐标点在原点（即还没发生触碰时）则绘制第一页
		 */
		if (mPointX == 0 && mPointY == 0) {
			canvas.drawBitmap(mBitmaps.get(mBitmaps.size() - 1), 0, 0, null);
			return;
		}

		/*
		 * 判断触摸点是否在短边的有效区域内
		 */
		if (!mRegionShortSize.contains((int) mPointX, (int) mPointY)) {
			// 如果不在则通过x坐标强行重算y坐标
			mPointY = (float) (Math.sqrt((Math.pow(mViewWidth, 2) - Math.pow(mPointX, 2))) - mViewHeight);

			// 精度附加值避免精度损失
			mPointY = Math.abs(mPointY) + mValueAdded;
		}

		/*
		 * 缓冲区域判断
		 */
		float area = mViewHeight - mBuffArea;
		if (!isSlide && mPointY >= area) {
			mPointY = area;
		}

		/*
		 * 额，这个该怎么注释好呢……根据图来
		 */
		float mK = mViewWidth - mPointX;
		float mL = mViewHeight - mPointY;

		// 需要重复使用的参数存值避免重复计算
		float temp = (float) (Math.pow(mL, 2) + Math.pow(mK, 2));

		/*
		 * 计算短边长边长度
		 */
		float sizeShort = temp / (2F * mK);
		float sizeLong = temp / (2F * mL);

		float tempAM = mK - sizeShort;

		/*
		 * 根据长短边边长计算旋转角度并确定mRatio的值
		 */
		if (sizeShort < sizeLong) {
			mRatio = Ratio.SHORT;
			float sin = tempAM / sizeShort;
			mDegrees = (float) (Math.asin(sin) / Math.PI * 180);
		} else {
			mRatio = Ratio.LONG;
			float cos = mK / sizeLong;
			mDegrees = (float) (Math.acos(cos) / Math.PI * 180);
		}

		if (sizeLong > mViewHeight) {
			// 计算……额……按图来AN边~
			float an = sizeLong - mViewHeight;

			// 三角形AMN的MN边
			float largerTrianShortSize = an / (sizeLong - (mViewHeight - mPointY)) * (mViewWidth - mPointX);

			// 三角形AQN的QN边
			float smallTrianShortSize = an / sizeLong * sizeShort;

			/*
			 * 计算参数
			 */
			float topX1 = mViewWidth - largerTrianShortSize;
			float topX2 = mViewWidth - smallTrianShortSize;
			float btmX2 = mViewWidth - sizeShort;

			// 计算曲线起点
			float startXBtm = btmX2 - CURVATURE * sizeShort;
			float startYBtm = mViewHeight;

			// 计算曲线终点
			float endXBtm = mPointX + (1 - CURVATURE) * (tempAM);
			float endYBtm = mPointY + (1 - CURVATURE) * mL;

			// 计算曲线控制点
			float controlXBtm = btmX2;
			float controlYBtm = mViewHeight;

			// 计算曲线顶点
			float bezierPeakXBtm = 0.25F * startXBtm + 0.5F * controlXBtm + 0.25F * endXBtm;
			float bezierPeakYBtm = 0.25F * startYBtm + 0.5F * controlYBtm + 0.25F * endYBtm;

			/*
			 * 生成带曲线的四边形路径
			 */
			mPath.moveTo(startXBtm, startYBtm);
			mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
			mPath.lineTo(mPointX, mPointY);
			mPath.lineTo(topX1, 0);
			mPath.lineTo(topX2, 0);

			/*
			 * 替补区域Path
			 */
			mPathTrap.moveTo(startXBtm, startYBtm);
			mPathTrap.lineTo(topX2, 0);
			mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
			mPathTrap.close();

			/*
			 * 底部月半圆Path
			 */
			mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
			mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
			mPathSemicircleBtm.close();

			/*
			 * 生成包含折叠和下一页的路径
			 */
			mPathFoldAndNext.moveTo(startXBtm, startYBtm);
			mPathFoldAndNext.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
			mPathFoldAndNext.lineTo(mPointX, mPointY);
			mPathFoldAndNext.lineTo(topX1, 0);
			mPathFoldAndNext.lineTo(mViewWidth, 0);
			mPathFoldAndNext.lineTo(mViewWidth, mViewHeight);
			mPathFoldAndNext.close();

			// 计算月半圆区域
			mRegionSemicircle = computeRegion(mPathSemicircleBtm);
		} else {
			/*
			 * 计算参数
			 */
			float leftY = mViewHeight - sizeLong;
			float btmX = mViewWidth - sizeShort;

			// 计算曲线起点
			float startXBtm = btmX - CURVATURE * sizeShort;
			float startYBtm = mViewHeight;
			float startXLeft = mViewWidth;
			float startYLeft = leftY - CURVATURE * sizeLong;

			// 计算曲线终点
			float endXBtm = mPointX + (1 - CURVATURE) * (tempAM);
			float endYBtm = mPointY + (1 - CURVATURE) * mL;
			float endXLeft = mPointX + (1 - CURVATURE) * mK;
			float endYLeft = mPointY - (1 - CURVATURE) * (sizeLong - mL);

			// 计算曲线控制点
			float controlXBtm = btmX;
			float controlYBtm = mViewHeight;
			float controlXLeft = mViewWidth;
			float controlYLeft = leftY;

			// 计算曲线顶点
			float bezierPeakXBtm = 0.25F * startXBtm + 0.5F * controlXBtm + 0.25F * endXBtm;
			float bezierPeakYBtm = 0.25F * startYBtm + 0.5F * controlYBtm + 0.25F * endYBtm;
			float bezierPeakXLeft = 0.25F * startXLeft + 0.5F * controlXLeft + 0.25F * endXLeft;
			float bezierPeakYLeft = 0.25F * startYLeft + 0.5F * controlYLeft + 0.25F * endYLeft;

			/*
			 * 限制右侧曲线起点
			 */
			if (startYLeft <= 0) {
				startYLeft = 0;
			}

			/*
			 * 限制底部左侧曲线起点
			 */
			if (startXBtm <= 0) {
				startXBtm = 0;
			}

			/*
			 * 根据底部左侧限制点重新计算贝塞尔曲线顶点坐标
			 */
			float partOfShortLength = CURVATURE * sizeShort;
			if (btmX >= -mValueAdded && btmX <= partOfShortLength - mValueAdded) {
				float f = btmX / partOfShortLength;
				float t = 0.5F * f;

				float bezierPeakTemp = 1 - t;
				float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
				float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
				float bezierPeakTemp3 = t * t;

				bezierPeakXBtm = bezierPeakTemp1 * startXBtm + bezierPeakTemp2 * controlXBtm + bezierPeakTemp3 * endXBtm;
				bezierPeakYBtm = bezierPeakTemp1 * startYBtm + bezierPeakTemp2 * controlYBtm + bezierPeakTemp3 * endYBtm;
			}

			/*
			 * 根据右侧限制点重新计算贝塞尔曲线顶点坐标
			 */
			float partOfLongLength = CURVATURE * sizeLong;
			if (leftY >= -mValueAdded && leftY <= partOfLongLength - mValueAdded) {
				float f = leftY / partOfLongLength;
				float t = 0.5F * f;

				float bezierPeakTemp = 1 - t;
				float bezierPeakTemp1 = bezierPeakTemp * bezierPeakTemp;
				float bezierPeakTemp2 = 2 * t * bezierPeakTemp;
				float bezierPeakTemp3 = t * t;

				bezierPeakXLeft = bezierPeakTemp1 * startXLeft + bezierPeakTemp2 * controlXLeft + bezierPeakTemp3 * endXLeft;
				bezierPeakYLeft = bezierPeakTemp1 * startYLeft + bezierPeakTemp2 * controlYLeft + bezierPeakTemp3 * endYLeft;
			}

			/*
			 * 替补区域Path
			 */
			mPathTrap.moveTo(startXBtm, startYBtm);
			mPathTrap.lineTo(startXLeft, startYLeft);
			mPathTrap.lineTo(bezierPeakXLeft, bezierPeakYLeft);
			mPathTrap.lineTo(bezierPeakXBtm, bezierPeakYBtm);
			mPathTrap.close();

			/*
			 * 生成带曲线的三角形路径
			 */
			mPath.moveTo(startXBtm, startYBtm);
			mPath.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
			mPath.lineTo(mPointX, mPointY);
			mPath.lineTo(endXLeft, endYLeft);
			mPath.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);

			/*
			 * 生成底部月半圆的Path
			 */
			mPathSemicircleBtm.moveTo(startXBtm, startYBtm);
			mPathSemicircleBtm.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
			mPathSemicircleBtm.close();

			/*
			 * 生成右侧月半圆的Path
			 */
			mPathSemicircleLeft.moveTo(endXLeft, endYLeft);
			mPathSemicircleLeft.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);
			mPathSemicircleLeft.close();

			/*
			 * 生成包含折叠和下一页的路径
			 */
			mPathFoldAndNext.moveTo(startXBtm, startYBtm);
			mPathFoldAndNext.quadTo(controlXBtm, controlYBtm, endXBtm, endYBtm);
			mPathFoldAndNext.lineTo(mPointX, mPointY);
			mPathFoldAndNext.lineTo(endXLeft, endYLeft);
			mPathFoldAndNext.quadTo(controlXLeft, controlYLeft, startXLeft, startYLeft);
			mPathFoldAndNext.lineTo(mViewWidth, mViewHeight);
			mPathFoldAndNext.close();

			/*
			 * 计算底部和右侧两月半圆区域
			 */
			Region regionSemicircleBtm = computeRegion(mPathSemicircleBtm);
			Region regionSemicircleLeft = computeRegion(mPathSemicircleLeft);

			// 合并两月半圆区域
			mRegionSemicircle.op(regionSemicircleBtm, regionSemicircleLeft, Region.Op.UNION);
		}

		// 根据Path生成的折叠区域
		mRegionFold = computeRegion(mPath);

		// 替补区域
		Region regionTrap = computeRegion(mPathTrap);

		// 令折叠区域与替补区域相加
		mRegionFold.op(regionTrap, Region.Op.UNION);

		// 从相加后的区域中剔除掉月半圆的区域获得最终折叠区域
		mRegionFold.op(mRegionSemicircle, Region.Op.DIFFERENCE);

		/*
		 * 计算下一页区域
		 */
		mRegionNext = computeRegion(mPathFoldAndNext);
		mRegionNext.op(mRegionFold, Region.Op.DIFFERENCE);

		drawBitmaps(canvas);
	}

	/**
	 * 绘制位图数据
	 *
	 * @param canvas
	 *            画布对象
	 */
	private void drawBitmaps(Canvas canvas) {
		// 绘制位图前重置isLastPage为false
		isLastPage = false;

		// 限制pageIndex的值范围
		mPageIndex = mPageIndex < 0 ? 0 : mPageIndex;
		mPageIndex = mPageIndex > mBitmaps.size() ? mBitmaps.size() : mPageIndex;

		// 计算数据起始位置
		int start = mBitmaps.size() - 2 - mPageIndex;
		int end = mBitmaps.size() - mPageIndex;

		/*
		 * 如果数据起点位置小于0则表示当前已经到了最后一张图片
		 */
		if (start < 0) {
			// 此时设置isLastPage为true
			isLastPage = true;

			// 并显示提示信息
			showToast("This is fucking lastest page");

			// 强制重置起始位置
			start = 0;
			end = 1;
		}

		/*
		 * 计算当前页的区域
		 */
		canvas.save();
		canvas.clipRegion(mRegionCurrent);
		canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
		canvas.restore();

		/*
		 * 计算折叠页的区域
		 */
		canvas.save();
		canvas.clipRegion(mRegionFold);

		canvas.translate(mPointX, mPointY);

		/*
		 * 根据长短边标识计算折叠区域图像
		 */
		if (mRatio == Ratio.SHORT) {
			canvas.rotate(90 - mDegrees);
			canvas.translate(0, -mViewHeight);
			canvas.scale(-1, 1);
			canvas.translate(-mViewWidth, 0);
		} else {
			canvas.rotate(-(90 - mDegrees));
			canvas.translate(-mViewWidth, 0);
			canvas.scale(1, -1);
			canvas.translate(0, -mViewHeight);
		}

		canvas.drawBitmap(mBitmaps.get(end - 1), 0, 0, null);
		canvas.restore();

		/*
		 * 计算下一页的区域
		 */
		canvas.save();
		canvas.clipRegion(mRegionNext);
		canvas.drawBitmap(mBitmaps.get(start), 0, 0, null);
		canvas.restore();
	}

	/**
	 * 默认显示
	 *
	 * @param canvas
	 *            Canvas对象
	 */
	private void defaultDisplay(Canvas canvas) {
		// 绘制底色
		canvas.drawColor(Color.WHITE);

		// 绘制标题文本
		mTextPaint.setTextSize(mTextSizeLarger);
		mTextPaint.setColor(Color.RED);
		canvas.drawText("FBI WARNING", mViewWidth / 2, mViewHeight / 4, mTextPaint);

		// 绘制提示文本
		mTextPaint.setTextSize(mTextSizeNormal);
		mTextPaint.setColor(Color.BLACK);
		canvas.drawText("Please set data use setBitmaps method", mViewWidth / 2, mViewHeight / 3, mTextPaint);
	}

	/**
	 * 通过路径计算区域
	 *
	 * @param path
	 *            路径对象
	 * @return 路径的Region
	 */
	private Region computeRegion(Path path) {
		Region region = new Region();
		RectF f = new RectF();
		path.computeBounds(f, true);
		region.setPath(path, new Region((int) f.left, (int) f.top, (int) f.right, (int) f.bottom));
		return region;
	}

	/**
	 * 计算滑动参数变化
	 */
	private void slide() {
		/*
		 * 如果滑动标识值为false则返回
		 */
		if (!isSlide) {
			return;
		}

		/*
		 * 如果当前页不是最后一页
		 * 如果是需要翻下一页
		 * 并且上一页已被做掉
		 */
		if (!isLastPage && isNextPage && (mPointX - mAutoSlideV_BL <= -mViewWidth)) {
			mPointX = -mViewWidth;
			mPointY = mViewHeight;
			mPageIndex++;
			invalidate();
		}

		/*
		 * 如果当前滑动标识为向右下滑动x坐标恒小于控件宽度
		 */
		else if (mSlide == Slide.RIGHT_BOTTOM && mPointX < mViewWidth) {
			// 则让x坐标自加
			mPointX += mAutoSlideV_BR;

			// 并根据x坐标的值重新计算y坐标的值
			mPointY = mStart_Y + ((mPointX - mStart_X) * (mViewHeight - mStart_Y)) / (mViewWidth - mStart_X);

			// 让SlideHandler处理重绘
			mSlideHandler.sleep(25);
		}

		/*
		 * 如果当前滑动标识为向左下滑动x坐标恒大于控件宽度的负值
		 */
		else if (mSlide == Slide.LEFT_BOTTOM && mPointX > -mViewWidth) {
			// 则让x坐标自减
			mPointX -= mAutoSlideV_BL;

			// 并根据x坐标的值重新计算y坐标的值
			mPointY = mStart_Y + ((mPointX - mStart_X) * (mViewHeight - mStart_Y)) / (-mViewWidth - mStart_X);

			// 让SlideHandler处理重绘
			mSlideHandler.sleep(25);
		}
	}

	/**
	 * 为isSlide提供对外的停止方法便于必要时释放滑动动画
	 */
	public void slideStop() {
		isSlide = false;
	}

	/**
	 * 提供对外的方法获取View内Handler
	 *
	 * @return mSlideHandler
	 */
	public SlideHandler getSlideHandler() {
		return mSlideHandler;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isNextPage = true;

		/*
		 * 获取当前事件点
		 */
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_UP:// 手指抬起时候
				if (isNextPage) {
				/*
				 * 如果当前事件点位于右下自滑区域
				 */
					if (x > mAutoAreaRight && y > mAutoAreaButtom) {
						// 当前为往右下滑
						mSlide = Slide.RIGHT_BOTTOM;

						// 摩擦吧骚年！
						justSlide(x, y);
					}

				/*
				 * 如果当前事件点位于左侧自滑区域
				 */
					if (x < mAutoAreaLeft) {
						// 当前为往左下滑
						mSlide = Slide.LEFT_BOTTOM;

						// 摩擦吧骚年！
						justSlide(x, y);
					}
				}
				break;
			case MotionEvent.ACTION_DOWN:
				isSlide = false;
			/*
			 * 如果事件点位于回滚区域
			 */
				if (x < mAutoAreaLeft) {
					// 那就不翻下一页了而是上一页
					isNextPage = false;
					mPageIndex--;
					mPointX = x;
					mPointY = y;
					invalidate();
				}
				downAndMove(event);
				break;
			case MotionEvent.ACTION_MOVE:
				downAndMove(event);
				break;
		}
		return true;
	}

	/**
	 * 处理DOWN和MOVE事件
	 *
	 * @param event
	 *            事件对象
	 */
	private void downAndMove(MotionEvent event) {
		if (!isLastPage) {
			mPointX = event.getX();
			mPointY = event.getY();

			invalidate();
		}
	}

	/**
	 * 在这光滑的地板上~
	 *
	 * @param x
	 *            当前触摸点x
	 * @param y
	 *            当前触摸点y
	 */
	private void justSlide(float x, float y) {
		// 获取并设置直线方程的起点
		mStart_X = x;
		mStart_Y = y;

		// OK要开始滑动了哦~
		isSlide = true;

		// 滑动
		slide();
	}

	/**
	 * 设置位图数据
	 *
	 * @param bitmaps
	 *            位图数据列表
	 */
	public synchronized void setBitmaps(List<Bitmap> bitmaps) {
		/*
		 * 如果数据为空则抛出异常
		 */
		if (null == bitmaps || bitmaps.size() == 0)
			throw new IllegalArgumentException("no bitmap to display");

		/*
		 * 如果数据长度小于2则GG思密达
		 */
		if (bitmaps.size() < 2)
			throw new IllegalArgumentException("fuck you and fuck to use imageview");

		mBitmaps = bitmaps;
		invalidate();
	}

	/**
	 * Toast显示
	 *
	 * @param msg
	 *            Toast显示文本
	 */
	private void showToast(Object msg) {
		Toast.makeText(mContext, msg.toString(), Toast.LENGTH_SHORT).show();
	}

	/**
	 * 处理滑动的Handler
	 */
	@SuppressLint("HandlerLeak")
	public class SlideHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// 循环调用滑动计算
			CurveView.this.slide();

			// 重绘视图
			CurveView.this.invalidate();
		}

		/**
		 * 延迟向Handler发送消息实现时间间隔
		 *
		 * @param delayMillis
		 *            间隔时间
		 */
		public void sleep(long delayMillis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	}
}

