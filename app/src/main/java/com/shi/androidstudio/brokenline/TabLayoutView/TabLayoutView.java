package com.shi.androidstudio.brokenline.TabLayoutView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.shi.androidstudio.brokenline.R;


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
