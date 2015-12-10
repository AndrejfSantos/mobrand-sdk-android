package com.mobrand.appwall.A;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.mobrand.appwall.event.NewAdsEvent;
import com.mobrand.sdk.core.MobrandCallback;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.NativeAd;
import com.mobrand.view.TabBarView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by rmateus on 02/12/15.
 */
public class AppWall extends AppCompatActivity {




    MobrandCore core = new MobrandCore();

    private List<Ad> nativeAdList = new ArrayList<>();


    private String placementId;
    private Toolbar mToolbarView;
    private TabBarView tabBar;

    public List<Ad> getNativeAdList() {
        return nativeAdList;
    }

    public static void start(Context context, String placement){

        Intent intent = new Intent(context, AppWall.class);

        intent.putExtra("placementId", placement);

        context.startActivity(intent);

    }

    public Toolbar getToolbar() {
        return mToolbarView;
    }

    @TargetApi(11)
    public void colorizeActionBar(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        placementId = getIntent().getStringExtra("placementId");


        setContentView(R.layout.appwall);


        mToolbarView = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarView);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tabbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        final ViewPager pager = (ViewPager) findViewById(R.id.content);
        final SavingPagerAdapter pagerAdapter = new SavingPagerAdapter(getSupportFragmentManager(), placementId);


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

        core.create(this);

        core.setCallback(new MobrandCallback() {

            @Override
            public void onReady(MobrandCore core) {

                core.getAdsAsync(placementId, new MobrandCore.GetAdsCallback() {

                    @Override
                    public void onSuccess(List<Ad> ads) {
                        nativeAdList.addAll(ads);
                        EventBus.getDefault().post(new NewAdsEvent(nativeAdList));

                    }

                    @Override
                    public void onFailure() {

                    }

                });

            }

            @Override
            public void onError() {

            }

        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        core.destroy();
    }

    public MobrandCore getMobrandCore() {
        return core;
    }


}
