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
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by SHI on 2017/2/23 14:29
 */
public class SimpleView_04 extends View {
    /**
     * 最大音量
     */
    private int maxVoice;
    /**
     * 当前音量
     */
    private int currentVoice;

    private Rect rectImage;

    public SimpleView_04(Context context) {
        this(context, null);
    }

    public SimpleView_04(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleView_04(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VoiceProgressBar, defStyleAttr, 0);
        int length = a.getIndexCount();
        for (int i = 0; i < length; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.VoiceProgressBar_maxVoice:
                    maxVoice = a.getInt(attr, 15);
                    break;
                case R.styleable.VoiceProgressBar_currentVoice:
                    currentVoice = a.getInt(attr, 5);
                    break;
            }
        }
        a.recycle();

        rectImage = new Rect();
    }

    //增加音量
    public void addVoice(){
        if(currentVoice < maxVoice) {
            currentVoice++;
            postInvalidate();
        }
    }

    //减少音量
    public void subVoice(){
        if(currentVoice > 0){
            currentVoice--;
            postInvalidate();
        }
    }

    //重写onDraw方法，绘制音量图案
    @Override
    protected void onDraw(Canvas canvas) {

        Bitmap bitmapSilence = scaleBitmap(R.mipmap.silence);
        Bitmap bitmapVoice = scaleBitmap(R.mipmap.voice);

        Paint mPaint = new Paint();

        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStrokeWidth(5); // 设置圆环的宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 定义线段断电形状为圆头
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); // 设置空心

        RectF oval = new RectF();                       //RectF对象
        oval.left = getPaddingLeft();            //左边
        oval.top = getPaddingTop();             //上边
        oval.right = getMeasuredWidth()-getPaddingRight();           //右边
        oval.bottom = getMeasuredHeight()-getPaddingBottom();          //下边

        mPaint.setColor(Color.WHITE);
        for (int i=0; i<maxVoice; i++){
            canvas.drawArc(oval, 40-20.77f*i, -5f, false, mPaint);    //绘制圆弧
        }

        mPaint.setColor(Color.BLACK);
        for (int i=0; i<currentVoice; i++){
            canvas.drawArc(oval, 40-20.77f*i, -5f, false, mPaint);    //绘制圆弧
        }

        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
        if(currentVoice == 0){

            rectImage.left = mWidth/2 - bitmapSilence.getWidth()/2;
            rectImage.right = mWidth/2 + bitmapSilence.getWidth()/2;
            rectImage.top = mHeight / 2 - bitmapSilence.getHeight() / 2;
            rectImage.bottom = mHeight/2 + bitmapSilence.getHeight()/2;
            canvas.drawBitmap(bitmapSilence,null,rectImage,mPaint);
        }else{
            rectImage.left = mWidth/2 - bitmapVoice.getWidth()/2;
            rectImage.right = mWidth/2 + bitmapVoice.getWidth()/2;
            rectImage.top = mHeight/2 - bitmapVoice.getHeight()/2;
            rectImage.bottom = mHeight/2 + bitmapVoice.getHeight()/2;
            canvas.drawBitmap(bitmapVoice,null,rectImage,mPaint);
        }


    }

    //对获取到的位图进行大小缩放
    private Bitmap scaleBitmap(int id)
    {

        Bitmap mTImage = BitmapFactory.decodeResource(getResources(), id);
        // 获得图片的宽高
        int width = mTImage.getWidth();
        int height = mTImage.getHeight();
        // 设置想要的大小
        int newWidth = (getMeasuredWidth()-getPaddingLeft()-getPaddingRight())*2/3;
        int newHeight = (getMeasuredHeight()-getPaddingTop()-getPaddingBottom())*2/3;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(mTImage, 0, 0, width, height, matrix, true);
    }

}
