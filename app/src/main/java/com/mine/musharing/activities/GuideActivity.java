package com.mine.musharing.activities;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mine.musharing.R;
import com.mine.musharing.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    // Views
    private ViewPager viewPager;

    private LinearLayout indicator;

    private Button finishButton;

    // Data
    private List<View> viewList;

    private ImageView[] indicatorImgs;

    private static final int GUIDE_PAGE_COUNT = 4;

    private int[] imgResArr = new int[]{R.mipmap.guide_0, R.mipmap.guide_1, R.mipmap.guide_2, R.mipmap.guide_3};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        // 隐藏状态栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();
        initData();
        initView();
        setupTransition();
    }

    private void bindViews() {
        viewPager = findViewById(R.id.guide_view_pager);
        indicator = findViewById(R.id.guide_indicator);
        finishButton = findViewById(R.id.guide_finish_button);

        finishButton.setOnClickListener(this::finishButtonOnClicked);
    }

    private void initData() {
        indicatorImgs = new ImageView[GUIDE_PAGE_COUNT];
        viewList = new ArrayList<View>(GUIDE_PAGE_COUNT);
        for (int i = 0; i < GUIDE_PAGE_COUNT; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.guide_item, null);
            ((ImageView) view.findViewById(R.id.guide_image)).setBackgroundResource(imgResArr[i]);
            viewList.add(view);
            indicatorImgs[i] = new ImageView(this);
            if (i == 0) {
                indicatorImgs[i].setBackgroundResource(R.drawable.ic_fiber_manual_record_cyan_100_24dp);
            } else {
                indicatorImgs[i].setBackgroundResource(R.drawable.ic_fiber_manual_record_grey_600_24dp);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.setMargins(20, 0, 0, 0);
                indicatorImgs[i].setLayoutParams(layoutParams);
            }
            indicator.addView(indicatorImgs[i]);
        }
    }

    private void initView() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == GUIDE_PAGE_COUNT - 1) {
                    finishButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setIndicator(int targetIndex) {
        for (int i = 0; i < indicatorImgs.length; i++) {
            indicatorImgs[i].setBackgroundResource(R.drawable.ic_fiber_manual_record_grey_600_24dp);
            if (targetIndex == i) {
                indicatorImgs[i].setBackgroundResource(R.drawable.ic_fiber_manual_record_cyan_100_24dp);
            }
        }
    }

    private void finishButtonOnClicked(View view) {
//        Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
//        Bundle translateBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(GuideActivity.this).toBundle();
//        startActivity(intent, translateBundle);
        finish();
    }

    /**
     * 设置 Activity 的转场动画
     */
    private void setupTransition() {
        TransitionSet transitionSet1 = Utility.getRandomTransitionSet();
        TransitionSet transitionSet2 = Utility.getRandomTransitionSet();
        TransitionSet transitionSet3 = Utility.getRandomTransitionSet();

        getWindow().setEnterTransition(transitionSet1);
        getWindow().setExitTransition(transitionSet2);
        getWindow().setReenterTransition(transitionSet3);
    }
}
