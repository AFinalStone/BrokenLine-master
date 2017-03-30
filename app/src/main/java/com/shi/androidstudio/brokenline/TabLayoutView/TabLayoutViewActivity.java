package com.shi.androidstudio.brokenline.TabLayoutView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.shi.androidstudio.brokenline.R;

import java.util.ArrayList;
import java.util.List;

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
