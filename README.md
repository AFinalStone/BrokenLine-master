上一篇文章简单实现了文字控件,这篇文章在此基础上面继续实现一个图片和文字混合展示的控件。
####一、在attrs.xml中添加自定义属性：
```xml

<?xml version="1.0" encoding="utf-8"?>
<resources>

    <declare-styleable name="CustomImageView">
        <attr name="titleText" />
        <attr name="titleTextColor" />
        <attr name="titleTextSize" />
        <attr name="image" />
        <attr name="imageScaleType" />

    </declare-styleable>

    <attr name="titleText" format="string" />
    <attr name="titleTextColor" format="color" />
    <attr name="titleTextSize" format="dimension" />
    <attr name="image" format="reference" />
    <attr name="imageScaleType">
        <enum name="fillXY" value="0" />
        <enum name="center" value="1" />
    </attr>
</resources>

```
####二、重写SimpleView_02控件的构造函数

```java
    public SimpleView_02(Context context) {
        //调用自身的构造方法二
        this(context, null);
        Log.e("SimpleView构造方法一", "被执行");
    }

    public SimpleView_02(Context context, AttributeSet attrs) {
        //调用自身的构造方法三
        this(context, attrs, 0);
        Log.e("SimpleView构造方法二", "被执行");
    }

    public SimpleView_02(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.e("SimpleView构造方法三", "被执行");
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyleAttr, 0);
        int length = a.getIndexCount();
        for (int i = 0; i < length; i++) {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.CustomImageView_image:
                   // mImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
                    mImage = scaleBitmap(a, attr);
                    break;
                case R.styleable.CustomImageView_imageScaleType:
                    mImageScale = a.getInt(attr, 0);
                    break;
                case R.styleable.CustomImageView_titleText:
                    mTitle = a.getString(attr);
                    break;
                case R.styleable.CustomImageView_titleTextColor:
                    mTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomImageView_titleTextSize:
                    mTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();

        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        // mPaint.setColor(mTitleTextColor);
        mRectText = new Rect();
        rect = new Rect();
        mPaint.getTextBounds(mTitle, 0, mTitle.length(), mRectText);

    }

    //对获取到的位图进行大小缩放
    private Bitmap scaleBitmap(TypedArray a, int attr)
    {

        Bitmap mTImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
        // 获得图片的宽高
        int width = mTImage.getWidth();
        int height = mTImage.getHeight();
        // 设置想要的大小
        int newWidth = 100;
        int newHeight = 100;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(mTImage, 0, 0, width, height, matrix, true);
    }
```

####三、重写SimpleView_02控件的onMeasure
```java
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// match_parent , accurate
                mWidth = specSize;
                Log.e("模式", "EXACTLY");
                break;
            default:
                int desireWidthByImg = getPaddingLeft() + getPaddingRight()+ mImage.getWidth();
                int desireWidthByTitle = getPaddingLeft() + getPaddingRight()+ mRectText.width();

                if (specMode == MeasureSpec.AT_MOST){// wrap_content
                    int desireWidth = Math.max(desireWidthByImg,desireWidthByTitle);
                    mWidth = Math.min(desireWidth,specSize);
                    Log.e("模式", "AT_MOST");
                }
                break;
        }
        //设置高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// match_parent , accurate
                mHeight = specSize;
                break;
            default:
                int desireHeight = getPaddingTop() + getPaddingBottom()+ mImage.getHeight()+mRectText.height();
                if (specMode == MeasureSpec.AT_MOST){// wrap_content
                    mHeight = Math.min(desireHeight,specSize);
                    Log.e("模式", "AT_MOST");
                }
                break;
        }

        setMeasuredDimension(mWidth,mHeight);
    }
```

####四、重写SimpleView_02控件的onDraw

```java
  @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, getMeasuredWidth(),getMeasuredHeight(), mPaint);

        rect.left = getPaddingLeft();
        rect.right = mWidth-getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = mHeight-getPaddingBottom();

        mPaint.setColor(mTextColor);
        mPaint.setStyle(Paint.Style.FILL);


        /**
         * 当前设置的宽度小于字体需要的宽度，将字体改为xxx...
         */
        if (mRectText.width() > mWidth)
        {
            TextPaint paint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitle, paint, (float) mWidth - getPaddingLeft() - getPaddingRight(),
                    TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingBottom(), mPaint);

        } else
        {
            //正常情况，将字体居中
            canvas.drawText(mTitle, mWidth / 2 - mRectText.width() * 1.0f / 2, mHeight - getPaddingBottom(), mPaint);
        }


        if(mImageScale == IMAGE_SCALE_FITXY){
            rect.bottom -= mRectText.height();
            canvas.drawBitmap(mImage,null,rect,mPaint);
        }else{
//            rect.left = mWidth/2-mImage.getWidth()/2;
//            rect.right = mWidth/2+mImage.getWidth()/2;
//            rect.top = mHeight/2+mImage.getHeight()/2;
//            rect.bottom = mHeight/2-mImage.getHeight()/2;
//            canvas.drawBitmap(mImage,null,rect,mPaint);

            //计算居中的矩形范围
            rect.left = mWidth / 2 - mImage.getWidth() / 2;
            rect.right = mWidth / 2 + mImage.getWidth() / 2;
            rect.top = (mHeight - mRectText.height()) / 2 - mImage.getHeight() / 2;
            rect.bottom = (mHeight - mRectText.height()) / 2 + mImage.getHeight() / 2;

            canvas.drawBitmap(mImage, null, rect, mPaint);
        }

    }
```
####五、效果图

![listview](https://github.com/AFinalStone/BrokenLine-master/blob/master/screenshot/1.png)<br>