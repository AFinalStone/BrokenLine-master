> 上一篇文章实现了一个[折线图控件](http://blog.csdn.net/abc6368765/article/details/58089567),这篇文章我们要实现一个类似TabLayout的自定义控件

####一、首先看一下我们这次要实现的效果图：

![效果图](/screenshot/GIF.gif)


####二、接着看一下如何使用这个控件

- TabLayoutViewActivity.java代码：

```java

public class TabLayoutViewActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<String> listTitle = new ArrayList<String>();
    private List<Fragment> listFragment = new ArrayList<Fragment>();
    private MyAdapter adapter;
    private TabLayoutView tabIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout_view);
        tabIndicatorView = (TabLayoutView) findViewById(R.id.tabIndicatorView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        for (int i = 0; i < 8; i++) {
            listTitle.add("标题" + i);
            TestViewFragment testFragment = new TestViewFragment();
            testFragment.initView(listTitle.get(i));
            listFragment.add(testFragment);
        }
        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabIndicatorView.setupWithViewPager(viewPager);
    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return listFragment.get(position);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listTitle.get(position);
        }
    }
}

```
- TabLayoutViewActivity对应的布局文件activity_tab_layout_view.xml

```xml

<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.shi.androidstudio.brokenline.TabLayoutView.TabLayoutView
        android:id="@+id/tabIndicatorView"
        android:layout_width="match_parent"
        android:layout_height="30dp"
        android:background="@android:color/white"
        app:tabTextSize="14sp"
        app:tabWidth="100dp"
        app:tabSelectedTextColor="@android:color/holo_purple"
        app:tabTextColor="@android:color/holo_red_dark"
        app:tabIndicatorWidth="50dp"
        app:tabIndicatorHeight="2dp"
        app:tabIndicatorColor="@android:color/holo_red_light"/>


    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="wrap_content"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="@android:color/white" />

</LinearLayout>

```

可以看出，这个TabLayoutView控件和官方提供的TabLayout控件使用流程非常相似，不过功能还是有待完善的，以后如果有需要我们继续完善功能的。
下面我贴出这个自定义控件的代码，大家也可以自行优化和完善。

####三、TabLayoutView控件书写流程介绍

- 在attrs.xml中添加自定义属性：

```xml

<?xml version="1.0" encoding="utf-8"?>
<resources>

    <declare-styleable name="TabLayoutView">
        <attr name="tabTextColor" format="color" />
        <attr name="tabSelectedTextColor" format="color" />
        <attr name="tabWidth" format="dimension" />
        <attr name="tabTextSize" format="dimension" />
        <attr name="tabIndicatorColor" format="color" />
        <attr name="tabIndicatorHeight" format="dimension" />
        <attr name="tabIndicatorWidth" format="dimension" />
    </declare-styleable>
</resources>

```
这里声明的这些属性名字基本和TabLayout的属性名称一样，目的就是为了让熟悉TabLayout控件的人能够很方便的使用这个控件。

- TabLayoutView控件需要使用一个名为item_tab_radiogroup的xml文件：

```xml

<HorizontalScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/hs_indicator"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:scrollbars="none" >

    <RelativeLayout
        android:id="@+id/relativeLayout_indicator"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <RadioGroup
            android:id="@+id/rg_indicator"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:orientation="horizontal" />

        <ImageView
            android:id="@+id/iv_indicator"
            android:layout_width="wrap_content"
            android:layout_height="1dp"
            android:layout_alignParentBottom="true" />
    </RelativeLayout>

</HorizontalScrollView>

```

在这里大概说一下实现流程，从布局文件中可以看出我们自定义的TabLayoutView其实就是一个组合控件，最外围是一个HorizontalScrollView，主要目的是为了TabLayoutView长度超出屏幕的时候可以左右滑动。
里面使用ImageView来作为TabLayoutView控件的下划线,在滑动或者选择不同条目的时候，通过给ImageView设置并开启属性动画来实现下划线滑动的功能，
使用RadioGroup，在代码中动态添加RadioButton来作为TabItem，并给RadioGroup设置监听，在里面给控制下划线滑动动画逻辑以及ViewPager的切换逻辑。具体如何实现还是请大家仔细阅读代码自己体会吧。

- TabLayoutView控件代码：

```java

/**
 * 自定义的TabIndicator
 * @author SHI
 * 2017-03-30 16:20:17
 */
public class TabLayoutView extends RelativeLayout {

    private HorizontalScrollView hs_indicator;
    private RadioGroup rg_indicator;
    private ImageView iv_indicator;
    private Context mContext;
    private int currentIndicatorLeft = 0;
    /**
     * radioButton 状态颜色选择集合
     **/
    private ColorStateList colorStateList;

    //新的属性
    private int tabWidth;
    private int tabTextSize;
    private int tabIndicatorColor;
    private int tabIndicatorHeight;
    private int tabIndicatorWidth;
    private ViewPager viewPager;

    public TabLayoutView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TabLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TabLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;

        TypedArray array = mContext.getTheme().obtainStyledAttributes(new int[] {
                android.R.attr.colorPrimary,
                android.R.attr.colorPrimaryDark,
                android.R.attr.colorAccent,
        });
        int colorPrimary = array.getColor( 0, 0);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayoutView, defStyleAttr, defStyleRes);

        //tabItem的属性初始化
        tabWidth = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabWidth, 0);
        tabTextSize = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabTextSize, 0);
        tabTextSize = px2sp(mContext, tabTextSize);
        int tabTextColor = typedArray.getColor(R.styleable.TabLayoutView_tabTextColor, Color.BLACK);
        int tabSelectedTextColor = typedArray.getColor(R.styleable.TabLayoutView_tabSelectedTextColor, colorPrimary);
        colorStateList = createColorStateList(tabTextColor,tabSelectedTextColor);

        //tabIndicator的属性初始化
        tabIndicatorColor = typedArray.getColor(R.styleable.TabLayoutView_tabIndicatorColor, colorPrimary);
        tabIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabIndicatorHeight, 5);
        tabIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabIndicatorWidth, 40);
        typedArray.recycle();

        //把我们的布局添加到当前控件中
        View view = View.inflate(context, R.layout.item_tab_radiogroup, null);
        hs_indicator = (HorizontalScrollView) view.findViewById(R.id.hs_indicator);
        rg_indicator = (RadioGroup) view.findViewById(R.id.rg_indicator);
        iv_indicator = (ImageView) view.findViewById(R.id.iv_indicator);
        addView(view);


        rg_indicator.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (rg_indicator.getChildAt(checkedId) != null) {
                    TranslateAnimation animation = new TranslateAnimation(
                    currentIndicatorLeft, rg_indicator.getChildAt(checkedId).getLeft(), 0f, 0f);
                    animation.setInterpolator(new LinearInterpolator());
                    animation.setDuration(100);
                    animation.setFillAfter(true);
                    //执行位移动画
                    iv_indicator.startAnimation(animation);

                    //记录当前 下标的距最左侧的 距离
                    currentIndicatorLeft = rg_indicator.getChildAt(checkedId).getLeft();

                    RadioButton radioButton1 = (RadioButton)rg_indicator
                            .getChildAt(checkedId);
                    RadioButton radioButton2 = (RadioButton) rg_indicator
                            .getChildAt(1);
                    if (radioButton1 != null && radioButton2 != null) {
                        hs_indicator.smoothScrollTo(
                                (checkedId > 1 ? radioButton1.getLeft() : 0)
                                        - radioButton2.getLeft(), 0);
                        if (viewPager != null){
                            viewPager.setCurrentItem(checkedId);
                        }
                    }
                }

            }
        });

    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /** 对TextView设置不同状态时其文字显示颜色 */
    private ColorStateList createColorStateList(int check, int normal) {
        int[] colors = new int[] {check, normal};
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_checked};
        states[1] = new int[] {};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /** 让我们的自定义控件和viewpager相关联 */
    public void setupWithViewPager(@Nullable ViewPager viewPager){

        this.viewPager = viewPager;
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        for (int position = 0; position < pagerAdapter.getCount(); position++) {
            RadioButton rb = new RadioButton(mContext);
            rb.setText(pagerAdapter.getPageTitle(position));
            rb.setId(position);
            RadioGroup.LayoutParams layoutParam = new RadioGroup.LayoutParams(
            new RadioGroup.LayoutParams(tabWidth, RadioGroup.LayoutParams.MATCH_PARENT));
            rb.setLayoutParams(layoutParam);
            rb.setTextSize(tabTextSize);
            rb.setTextColor(colorStateList);
            rb.setGravity(Gravity.CENTER);
            rb.setBackgroundColor(Color.TRANSPARENT);
            rb.setButtonDrawable(android.R.color.transparent);
            rg_indicator.addView(rb);
        }

        android.view.ViewGroup.LayoutParams indicator_LayoutParams = iv_indicator.getLayoutParams();
        indicator_LayoutParams.width = tabWidth;
        indicator_LayoutParams.height = tabIndicatorHeight;
        int padding = (tabWidth-tabIndicatorWidth)/2;
        iv_indicator.setLayoutParams(indicator_LayoutParams);
        iv_indicator.setPadding(padding, 0, padding, 0);
        ColorDrawable colorDrawable = new ColorDrawable(tabIndicatorColor);
        iv_indicator.setImageDrawable(colorDrawable);
        postInvalidate();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(final int position) {
                setCurrentSelectItem(position);
            }

            @Override
            public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(final int position) {
            }
        });
    }


    /**
     * 设置当前选中条目
     *
     * @param currentPosition
     */
    public void setCurrentSelectItem(int currentPosition) {
        RadioButton radioButton = ((RadioButton) rg_indicator
                .getChildAt(currentPosition));
        if (radioButton != null)
            radioButton.performClick();
    }

    /**
     * 获取当前选中条目的position
     *
     * @return
     */
    public int getCurrentSelectPosition() {
        int currentIdPosition = rg_indicator.getCheckedRadioButtonId();
        return currentIdPosition;
    }

}

```

OK，这样简单几步，一个自定义TabLayoutView控件就应运而生了。代码还是比较简陋的，有兴趣的同学可以下载下来看一下。
最后附上项目下载地址：戳我进入