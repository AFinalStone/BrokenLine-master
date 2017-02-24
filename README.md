上一篇文章实现了一个动态的圆弧控件,这篇文章在此基础上面继续实现一个简单的音量增减控件

####一、首先看一下我们这次要实现的效果图：

![效果图](https://github.com/AFinalStone/BrokenLine-master/blob/master/screenshot/GIF_Voice.gif)<br>

####二、在attrs.xml中添加自定义属性：

```xml

<?xml version="1.0" encoding="utf-8"?>
<resources>

    <attr name="maxVoice" format="integer"/>
    <attr name="currentVoice" format="integer"/>

    <declare-styleable name="VoiceProgressBar">
        <attr name="maxVoice" />
        <attr name="currentVoice" />
    </declare-styleable>
</resources>

```
####三、对应的布局文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="horizontal"
    android:gravity="center_horizontal"
    android:padding="20dp"
    android:background="@mipmap/beautiful"
    tools:context="com.shi.androidstudio.brokenline.MainActivity">

    <TextView
        android:id="@+id/tv_subVoice"
        android:layout_width="50dp"
        android:layout_height="100dp"
        android:gravity="center"
        android:text="-"
        android:textSize="26sp" />

    <com.shi.androidstudio.brokenline.SimpleView_04
        android:id="@+id/simpleView_04"
        android:layout_width="150dp"
        android:layout_height="110dp"
        android:background="#DD3F3F3F"
        android:paddingLeft="30dp"
        android:paddingRight="30dp"
        android:paddingTop="10dp"
        android:paddingBottom="10dp"
        app:currentVoice="5"
        app:maxVoice="13" />

    <TextView
        android:id="@+id/tv_addVoice"
        android:layout_width="50dp"
        android:layout_height="100dp"
        android:gravity="center"
        android:text="+"
        android:textSize="20sp" />
</LinearLayout>

```

####四、SimpleView_04自定义控件代码：

```java

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

```

到这里基本就实现了我们需要的自定义控件了。