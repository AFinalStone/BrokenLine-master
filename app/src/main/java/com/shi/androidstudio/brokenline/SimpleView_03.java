package com.shi.androidstudio.brokenline;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by SHI on 2017/2/23 14:29
 */
public class SimpleView_03 extends View {
    /**
     * 第一圈的颜色
     */
    private int mFirstColor;
    /**
     * 第二圈的颜色
     */
    private int mSecondColor;
    /**
     * 圈的宽度
     */
    private float mCircleLength = 100;
    /**
     * 画笔
     */
    private Paint mPaint_01;
    /**
     * 当前进度
     */
    private int mProgress = 1;

    /**
     * 速度
     */
    private int mSpeed;

    /**
     * 是否应该开始下一个
     */
    private boolean isNext = false;


    public SimpleView_03(Context context) {
        this(context, null);
    }

    public SimpleView_03(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleView_03(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyleAttr, 0);
        int length = a.getIndexCount();
        for (int i = 0; i < length; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomProgressBar_firstColor:
                   mFirstColor =  a.getColor(attr,Color.BLACK);
                    break;
                case R.styleable.CustomProgressBar_secondColor:
                    mSecondColor =  a.getColor(attr,Color.WHITE);
                    break;
                case R.styleable.CustomProgressBar_circleLength:
                    mCircleLength = a.getDimension(attr, 10.00f);
                    break;
                case R.styleable.CustomProgressBar_speed:
                    mSpeed = a.getInt(attr,10);
                    break;
            }
        }
        a.recycle();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    mProgress = mProgress % 360;
                    postInvalidate();
                    mProgress++;
                    if (mProgress == 360) {
                        isNext = !isNext;
                    }
                    SystemClock.sleep(mSpeed);
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint_01 = new Paint();
        mPaint_01.setAntiAlias(true);                   //设置画笔为无锯齿
        mPaint_01.setColor(mFirstColor);                  //设置画笔颜色
        canvas.drawColor(Color.WHITE);                  //白色背景
        mPaint_01.setStrokeWidth((float) 20);           //线宽
        mPaint_01.setStyle(Paint.Style.STROKE);

        int centerX = getMeasuredWidth()/2;
        int centerY = getMeasuredHeight()/2;

        RectF oval = new RectF();                       //RectF对象
        oval.left = centerX - mCircleLength;            //左边
        oval.top = centerY - mCircleLength;             //上边
        oval.right = centerX + mCircleLength;           //右边
        oval.bottom = centerY + mCircleLength;          //下边

        Paint mPaint_02 = new Paint();
        mPaint_02.setAntiAlias(true);                   //设置画笔为无锯齿
        mPaint_02.setColor(mSecondColor);                //设置画笔颜色
        canvas.drawColor(Color.WHITE);                  //白色背景
        mPaint_02.setStrokeWidth((float) 20);           //线宽
        mPaint_02.setStyle(Paint.Style.STROKE);

        if(!isNext){
            canvas.drawCircle(centerX,centerY,mCircleLength,mPaint_01);
            canvas.drawArc(oval, 0, mProgress, false, mPaint_02);    //绘制圆弧
        }else{
            canvas.drawCircle(centerX,centerY,mCircleLength,mPaint_02);
            canvas.drawArc(oval, 0, mProgress, false, mPaint_01);    //绘制圆弧
        }

    }

}
