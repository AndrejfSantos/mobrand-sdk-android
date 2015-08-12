package com.mobrand.mobrandsample;


import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import com.mobrand.sdk.R;
import com.mobrand.utils.ColorUtils;
import com.mobrand.view.TabBarView;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;

import retrofit.RestAdapter;


public class MainActivity extends AppCompatActivity {

    private TabBarView tabBar;
    private Toolbar mToolbarView;

    RestAdapter configGetter = new RestAdapter.Builder().setEndpoint("http://api.mobrand.net/").build();


    public static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // if we can’t find it in the manifest, just return null
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImpressionsEngine.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        ImpressionsEngine.init("http://api.mobrand.net/");

        String appid = getMetadata(this, "mobrand_appid");
        String placement = getIntent().getStringExtra("placementid");

        placement = "App Wall";
        if(appid == null || placement == null){
            throw new IllegalStateException("appid or placementid is null - appid:" + appid + " placement:" + placement);
        }

        mToolbarView = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarView);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tabbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        final ViewPager pager = (ViewPager) findViewById(R.id.content);
        final SavingPagerAdapter pagerAdapter = new SavingPagerAdapter(getSupportFragmentManager(), appid, placement);
        pager.setAdapter(pagerAdapter);

        pager.setOffscreenPageLimit(4);

        tabBar = (TabBarView) getSupportActionBar().getCustomView();

        tabBar.setOnTabClickedListener(new TabBarView.OnTabClickedListener() {
            @Override
            public void onTabClicked(int index) {
                pager.setCurrentItem(index);
            }
        });


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (tabBar != null) {
                    tabBar.setOffset(positionOffset);
                    tabBar.setSelectedTab(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (pagerAdapter != null && pagerAdapter.getRegisteredFragment(position) != null) {
                    for (int i = 0; i < pagerAdapter.getCount(); i++) {
                        if (pagerAdapter.getRegisteredFragment(i) != null) {
                            pagerAdapter.getRegisteredFragment(i).setUserVisibleHint(position == i);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }


    public Toolbar getToolbar() {
        return mToolbarView;
    }


    public void colorizeActionBar(int color) {

        int oldColor = getResources().getColor(R.color.red);

        if (getToolbar() != null) {
            Drawable toolbarDrawable = getToolbar().getBackground();
            if (toolbarDrawable != null && toolbarDrawable instanceof ColorDrawable) {
                oldColor = ColorUtils.getBackgroundColor((ColorDrawable) toolbarDrawable);
            }

        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), oldColor, color);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int color = (Integer) valueAnimator.getAnimatedValue();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ColorUtils.darkenColor(color));
                }
                getToolbar().setBackgroundColor(color);
            }
        });
        colorAnimation.setDuration(300);
        colorAnimation.start();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);

    }



}
