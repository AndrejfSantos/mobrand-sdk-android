package com.mobrand.appwall.classic;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mobrand.appwall.adapters.SavingPagerAdapter;
import com.mobrand.appwall.event.NewAdsEvent;
import com.mobrand.model.DefaultGroups;
import com.mobrand.model.GroupStringsMapJson;
import com.mobrand.model.Webservices;
import com.mobrand.sdk.core.Defaults;
import com.mobrand.sdk.core.GetAdsCallback;
import com.mobrand.sdk.core.MobrandCallback;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.event.MobrandLifecycle;
import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.CategoryEnum;
import com.mobrand.appwall.view.TabBarView;
import com.mobrand.appwall.view.TabView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mobrand.model.Constants.*;


/**
 * Created by rmateus on 02/12/15.
 */
public class AppWall extends AppCompatActivity {


    private MobrandCore mCore;

    private List<Ad> mNativeAdList = new ArrayList<>();

    private String mPlacementId;
    private Toolbar mToolbarView;
    private TabBarView mTabBar;
    private ViewPager mPager;
    private SavingPagerAdapter mPagerAdapter;
    private SparseArrayCompat<List<String>> headersList = DefaultGroups.getSparseArray();
    private boolean asInterstitial;

    public List<Ad> getmNativeAdList() {
        return mNativeAdList;
    }

    public static AppwallFactory.AppwallBuilder build(Context context, String mPlacementId){
        return new AppwallFactory.AppwallBuilder(mPlacementId, context);
    }

    public static void start(Context context, String placement){
        start(context, placement, null, false);
    }

    protected static void start(Context context, String placement, MobrandLifecycle callback, boolean asInterstitial) {

        Intent intent = new Intent(context, AppWall.class);

        intent.putExtra("placementid", placement);
        intent.putExtra("asInterstitial", asInterstitial);

        context.startActivity(intent);

        if (callback != null) {
            EventBus.getDefault().register(callback);
        }

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

        try {
            setContentView(R.layout.mb_appwall_content);
            loading(true);

            mCore = new MobrandCore(this);
            mPlacementId = getIntent().getStringExtra("placementid");
            mToolbarView = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbarView);


            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.mb_tabbar);

            TypedArray ta = getTheme().obtainStyledAttributes(new int[]{R.attr.mb_appwallAsInterstitial});
            asInterstitial = ta.getBoolean(0, true);
            ta.recycle();

            if(!asInterstitial) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            }

            mPager = (ViewPager) findViewById(R.id.content);
            mPager.setOffscreenPageLimit(4);
            mTabBar = (TabBarView) getSupportActionBar().getCustomView();
            mTabBar.setOnTabClickedListener(new TabBarView.OnTabClickedListener() {
                @Override
                public void onTabClicked(int index) {
                    mPager.setCurrentItem(index);
                }
            });

           mCore.create(new MobrandCallback() {

               @Override
               public void onReady(final MobrandCore core) {

                   String staticEndpoint = core.getConfig().getStaticEndpoint();
                   RestAdapter adapter = Defaults.getDefaultRestAdapter(AppWall.this, staticEndpoint);

                   final Webservices webservices = adapter.create(Webservices.class);

                   webservices.getAndroidGroups(Locale.getDefault().getLanguage(), new Callback<GroupStringsMapJson>() {
                       @Override
                       public void success(GroupStringsMapJson groupStringsMapJson, Response response) {
                           headersList = groupStringsMapJson.convertToSparseArray();
                           init(core);
                       }

                       @Override
                       public void failure(RetrofitError error) {
                           init(core);
                       }
                   });


               }

               @Override
               public void onError() {
                   ((TextView) findViewById(R.id.error_tv)).setText(R.string.mobrand_appwall_error_network);
                   loading(false);
               }

           });


            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey("lastcolor")) {
                    colorizeActionBar(savedInstanceState.getInt("lastcolor"));
                }
            }else{
                colorizeActionBar(getResources().getColor(R.color.mobrand));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mPagerAdapter != null && mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem()) != null) {
            outState.putInt("lastcolor", ((FragmentPage) mPagerAdapter.getRegisteredFragment(mPager.getCurrentItem())).getAppBarColor());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (asInterstitial) {
            getMenuInflater().inflate(R.menu.interstitialmenu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }



    private void init(MobrandCore core) {
        EventBus.getDefault().post(new MobrandLifecycle.Event(MobrandLifecycle.EnumLifecycle.CREATE));
        core.getAdsAsync(mPlacementId, new GetAdsCallback() {

            @Override
            public void onSuccess(List<Ad> ads) {

                final Context context = AppWall.this;

                boolean apps = false;
                boolean games = false;
                boolean mobrando = false;

                for (Ad ad : ads) {

                    CategoryEnum categoryEnum = CategoryEnum.forValue(ad.getCategory());

                    switch (categoryEnum) {
                        case APPS:
                            apps = true;
                            break;
                        case GAMES:
                            games = true;
                            break;
                        case MOBRAND:
                            mobrando = true;
                            break;
                    }

                    mNativeAdList.add(ad);

                }


                if (mNativeAdList.isEmpty()) {
                    ((TextView) findViewById(R.id.error_tv)).setText(R.string.mobrand_appwall_noads);
                } else {
                    try {
                        initPager(context, apps, games, mobrando);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new NewAdsEvent(mNativeAdList, headersList));
                    EventBus.getDefault().post(new MobrandLifecycle.Event(MobrandLifecycle.EnumLifecycle.ADSRECEIVED));

                }


                loading(false);


            }

            @Override
            public void onFailure() {
                ((TextView) findViewById(R.id.error_tv)).setText(R.string.mobrand_appwall_error);
                loading(false);
            }

        });
    }

    private void loading(boolean show) {
        View progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);


        Animation animation = AnimationUtils.loadAnimation(this, show ? android.R.anim.fade_in : android.R.anim.fade_out);
        progressBar.startAnimation(animation);


    }

    private void initPager(Context context, boolean apps, boolean games, boolean mobrand) {
        List<Integer> categories = new ArrayList<>();

        if (apps || games) {
            categories.add(GENERIC);
            addTabView(context, R.drawable.ic_action_arrow);
        }

        if (games) {
            addTabView(context, R.drawable.ic_action_games);
            categories.add(GAMES);
        }

        if (apps) {
            addTabView(context, R.drawable.ic_action_apps);
            categories.add(APPS);
        }

        if (mobrand) {
            addTabView(context, R.drawable.ic_mobrandowhite);
            categories.add(MOBRAND);
        }

        mPagerAdapter = new SavingPagerAdapter(getSupportFragmentManager(), mPlacementId, categories);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(onPageChangeListener);
    }


    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mTabBar != null) {
                mTabBar.setOffset(positionOffset);
                mTabBar.setSelectedTab(position);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mPagerAdapter != null && mPagerAdapter.getRegisteredFragment(position) != null) {
                for (int i = 0; i < mPagerAdapter.getCount(); i++) {
                    if (mPagerAdapter.getRegisteredFragment(i) != null) {
                        mPagerAdapter.getRegisteredFragment(i).setUserVisibleHint(position == i);
                    }
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };




    private void addTabView(Context context, int iconRes) {
        TabView tabView = new TabView(context);
        tabView.setIcon(iconRes);
        mTabBar.addView(tabView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == android.R.id.home || itemId == R.id.home || itemId == R.id.close) {
            finish();
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCore.destroy();
        EventBus.getDefault().post(new MobrandLifecycle.Event(MobrandLifecycle.EnumLifecycle.DESTROY));

    }

    public MobrandCore getMobrandCore() {
        return mCore;
    }


    public SparseArrayCompat<List<String>> getHeadersList() {
        return headersList;
    }

}
