package com.tianshaokai.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 时钟 摆动的view
 */
public class Pendulum extends View {

    private Paint linePaint;
    private int width;
    private int height;

    private Path linePath;          //用来绘制静态部分的Path
    private Path bigCirclePath;     //用来测量大圆的Path
    private Path rotateLinePath;    //用来绘制动态部分的Path

    private int stroke = 5;         //线段的宽度
    private int r = 20;             //圆圈的半径
    private int length = 400;       //线的长度
    private int number = 5;         //球的个数（奇数，偶数感觉丑就没实现）
    private static int angle = 50;  //最大旋转角度

    // 第一个参数表示角度；负数表示左边球旋转的角度，正数表示右边球旋转的角度
    // +angle表示右侧球偏离最大的角度为30度
    // -angle表示左侧球偏离最大的角度为30度
    // 第二个参数表示方向；-1表示从右往左摆动，1表示从左往右摆动
    private float[] degree = new float[]{angle, -1};

    private float t = 30f;          //时间；可以用来控制速率，t越小，摆钟越慢；t越大，摆钟越快
    private float cosO;             //cosθ,是个固定值
    private float gr2;              //2gr，是个固定值
    private int interval = 40;      //时间间隔
    private long last;              //上次的时间
    private long now;               //当前的时间

    public Pendulum(Context context) {
        super(context);
        initPaint();
        calCosOAnd2gr();
    }

    public Pendulum(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        calCosOAnd2gr();
    }

    public Pendulum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        calCosOAnd2gr();
    }

    private void initPaint() {
        last = System.currentTimeMillis();
        //这里不想弄多个Paint,就用一个Paint来替代了，如果有需要，可以增加Paint来绘制指定的图形
        linePaint = new Paint();
        linePaint.setStrokeWidth(stroke);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setColor(0xff4897fe);
    }

    /**
     * 用来计算cosθ和2gr
     */
    private void calCosOAnd2gr() {
        //这里为了避免cosα-cosθ=0的情况，所以+0.1
        cosO = (float) Math.cos((angle + 0.1f) * Math.PI / 180);
        //2倍的重力加速度乘以半径
        gr2 = (float) (9.8 * r * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(width / 2, height / 2);

        drawPic(canvas);
        rotate(canvas);
    }


    /**
     * 绘制静态图形
     *
     * @param canvas
     */
    private void drawPic(Canvas canvas) {
        if (number < 1) {
            throw new IllegalArgumentException("数量不能小于1");
        }
        int x;
        if (number % 2 == 1) {
            //奇数的情况
            //用来确定最外层的位置，例如：如果number为3，leftNumber为1
            //                          number为5，leftNumber为2
            //                          number为7，leftNumber为3
            int leftNumber = number / 2;

            for (int i = -leftNumber; i <= leftNumber; i++) {
                if (isRight()) {
                    //最右侧在摇摆
                    if (i == leftNumber)
                        continue;
                } else if (!isRight()) {
                    //最左侧的在摇摆
                    if (i == -leftNumber)
                        continue;
                }
                //计算圆心的横坐标x
                x = 2 * r * i;
                if (linePath == null)
                    linePath = new Path();
                linePath.reset();
                //move到圆心(更准确的坐标为(x,-r),圆绘制出来会把部分线段覆盖；这里只是为了方便表示，不再增加多余的点)
                linePath.moveTo(x, 0);
                //画直线到顶点，（顶点离圆心= 线段的长度 + 半径）
                linePath.lineTo(x, -(r + length));
                //绘制直线
                linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawPath(linePath, linePaint);
                //绘制圆圈，为了不重合，使用FILL，不绘制线的宽度
                linePaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(x, 0, r, linePaint);
            }
        } else if (number % 2 == 0) {
            //偶数
            throw new IllegalArgumentException("偶数太丑，没有绘制");
        }
    }

    /**
     * 绘制旋转的图形
     *
     * @param canvas
     */
    private void rotate(Canvas canvas) {
        //左侧球运动和右侧球运动是对称的，使用direction（值为+1或-1）来做标记
        int direction;
        if (isRight()) {
            //右侧球运动，+1
            direction = 1;
        } else {
            //左侧球于东，-1
            direction = -1;
        }
        //measure.getPosTan()中不接受负数，这里需要取绝对值
        float nowDegree = Math.abs(degree[0]);

        linePaint.setStyle(Paint.Style.STROKE);
        //确定单侧外层图片的个数
        int pointNumber = number / 2;
        //确定静态圆形的横坐标，与drawPic中的（x = 2 * r * i）相似
        int x = 2 * r * pointNumber * direction;
        //用来确定大圆圆心的坐标，同时也是线段顶点的坐标
        float[] topPoint = new float[]{x, -(r + length)};

        int totalLength = length + r;
        if (bigCirclePath == null)
            bigCirclePath = new Path();
        bigCirclePath.reset();
        //rectF是用来绘制弧形的：以线段的顶点为圆心，length + r为半径画弧形
        RectF rectF = new RectF(topPoint[0] - totalLength, topPoint[1] - totalLength, topPoint[0] + totalLength, topPoint[1] + totalLength);
        //绘制1/4个圆的弧形
        bigCirclePath.addArc(rectF, 90, -90 * direction);

        //用来确定旋转nowDegree时的边界坐标；
        float[] rotatePoint = new float[2];
        PathMeasure measure = new PathMeasure(bigCirclePath, false);
        //此时，rotatePoint的坐标就为我们图中小圆圈圆心的坐标
        measure.getPosTan(measure.getLength() * (nowDegree) / 90, rotatePoint, null);
        //到现在为止，我们已经知道了圆心的坐标以及线段顶点的坐标了。
        //下面，我们就链接这个两个点，并以rotatePoint为圆心画圆

        //画线段
        if (rotateLinePath == null)
            rotateLinePath = new Path();
        rotateLinePath.reset();
        rotateLinePath.moveTo(topPoint[0], topPoint[1]);
        rotateLinePath.lineTo(rotatePoint[0], rotatePoint[1]);
        canvas.drawPath(rotateLinePath, linePaint);
        //画圆
        linePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(rotatePoint[0], rotatePoint[1], r, linePaint);

        //显示文字用的，不用理会
//        linePaint.setTextSize(40);
//        canvas.drawText("当前的改变量：" + rotateAngle(), -200, 100, linePaint);

        //degree[1]表示方向，当为1时，表示从左向右运动，那么degree[0]需要不断增加（这是我规定的；当然要修改的话，可以根据情况来修改，估计修改时会晕菜一段时间）
        if (degree[1] == 1) {
            //从总往右,degree增大
            if (degree[0] < angle) {
                //计算需要转动的角度
                float changeAngle = rotateAngle();
                //改变当前角度的值
                degree[0] = degree[0] + changeAngle;
                if (degree[0] >= angle) {
                    //转过头情况的处理：掉头，放到起始位置
                    degree[0] = angle;
                    degree[1] = -1;
                }
                refreshPic();
            }
            //当达到最右侧时，方向翻转
            if (degree[0] >= angle) {
                degree[1] = -1;
            }
        }
        //degree[1]表示方向，当为-1时，表示从右向左运动，那么degree[0]需要不断减小（这是我规定的；当然要修改的话，可以根据情况来修改，估计修改时会晕菜一段时间）
        else if (degree[1] == -1) {
            //从右往左，degree减小
            if (degree[0] > -angle) {
                //计算需要转动的角度
                float changeAngle = rotateAngle();
                //改变当前角度的值
                degree[0] = degree[0] - changeAngle;
                if (degree[0] <= -angle) {
                    //转过头情况的处理：掉头，放到起始位置
                    degree[0] = -angle;
                    degree[1] = 1;
                }
                refreshPic();
            }
            //当达到最左侧时，方向翻转
            if (degree[0] <= -angle) {
                degree[1] = 1;
            }
        }
    }

    private void refreshPic() {
        now = System.currentTimeMillis();
        long i = now - last;
        long post = interval - i;
        postInvalidateDelayed(post);
        if (post < 0) {
//            Log.e("ddd", "p-->" + post);
        }
        last = now;
    }

    /**
     * 计算当前需要转动的角度
     *
     * @return
     */
    private float rotateAngle() {
        //计算当前的速率
        float v = (float) Math.sqrt(gr2 * (Math.cos(Math.abs(degree[0]) * Math.PI / 180) - cosO));
        //计算需要改变的弧度
        float changedAngle = t * v / length;
        if (changedAngle == 0) {
            changedAngle = 0.001f;
        }
        return changedAngle;
    }

    /**
     * 判断是否是右侧的圆球在动
     *
     * @return  true    -->右侧的圆球在动
     *          false   -->左侧的圆球在动
     */
    private boolean isRight() {
        boolean flag = false;
        //degree[0]大于0，表示右侧球在动
        //degree[1]小于0，表示左侧球在动
        if (degree[0] > 0) {
            flag = true;
        } else if (degree[0] < 0) {
            flag = false;
        } else if (degree[0] == 0) {
            //如果degree等于0，需要根据方向来判断哪个求在动
            //degree[1]等于-1表示：球是从右往左在运动，此时，球的速度 v-->0，但还是右侧的球在动
            if (degree[1] == -1) {
                flag = true;
            }
            //与上面的情况相反，是左侧的球在动
            else if (degree[1] == 1) {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
}
