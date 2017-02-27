package com.shi.androidstudio.brokenline;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by SHI on 2017/2/23 14:29
 */
public class SimpleView_05 extends View {

    //X坐标尺码单位长度
    private int tick_X;
    //Y坐标尺码单位长度
    private int tick_Y;

    private int number_Y = 10;
    private int number_X = 10;


    private int start_X = 30;
    private int start_Y = 30;
    private int tickLength = 5;

    //分割线长度
    private int [] xArray = {1,2,3,4,5,6,7};
    private int [] yArray = {1,4,4,2,5,6,4};

    public SimpleView_05(Context context) {
        this(context, null);
    }

    public SimpleView_05(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleView_05(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BrokenLine, defStyleAttr, 0);
        int length = a.getIndexCount();
        for (int i = 0; i < length; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.BrokenLine_number_X:
                    number_X = a.getInt(attr,0);
                    Log.e("BrokenLine_number_X", ""+number_X);
                    break;

                case R.styleable.BrokenLine_number_Y:
                    number_Y = a.getInt(attr, 0);
                    Log.e("BrokenLine_number_Y", ""+number_Y);
                    break;
            }
        }
        a.recycle();
    }



    //重写onDraw方法，绘制音量图案
    @Override
    protected void onDraw(Canvas canvas) {

        Paint mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
        //减少10是为了防止绘制超出范围
        int width = getMeasuredWidth()-10;
        int height = getMeasuredHeight();
        Log.e("宽度", ""+width);
        tick_X = (width-start_X)/number_X;
        tick_Y = (height-start_Y)/number_Y;
        mPaint.setColor(Color.BLACK);
        //绘制坐标系Y轴
        canvas.drawLine(start_X, 0, start_X, height-start_Y, mPaint);
        //绘制坐标系X轴
        canvas.drawLine(start_X, height-start_Y, width, height-start_Y, mPaint);

        for (int i=1; i<=number_Y; i++){
            //绘制Y轴分割线
            canvas.drawLine(start_X, height-start_Y-tick_Y*i, start_X-tickLength, height-start_Y-tick_Y*i, mPaint);
            //绘制Y轴坐标描述,减少10分之9是为了防止Y轴出界
            canvas.drawText(""+10*i, 0, height-start_Y*9/10-tick_Y*i, mPaint);
        }
        for (int i=1; i<=number_X; i++){
            //绘制X轴分割线
            canvas.drawLine(start_X+tick_X*i, height-start_Y, start_X+tick_X*i, height-start_Y+tickLength, mPaint);
            //绘制X轴坐标描述
            canvas.drawText("Ps"+i, start_X/2+tick_X*i, height-start_Y/2, mPaint);
        }

        //绘制点集
        for (int i=0; i<xArray.length; i++){
            mPaint.setStrokeWidth(4);
            canvas.drawPoint(start_X+xArray[i]*tick_X, height-start_Y-yArray[i]*tick_Y, mPaint);
            if(i != 0){
                mPaint.setStrokeWidth(1);
                canvas.drawLine(start_X+xArray[i-1]*tick_X,height-start_Y-yArray[i-1]*tick_Y
                        ,start_X+xArray[i]*tick_X, height-start_Y-yArray[i]*tick_Y,mPaint);
            }
        }

    }


}
