package com.mobrand.simplyred;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobrand.sdk.core.ClickCallback;
import com.mobrand.sdk.core.GetAdCallback;
import com.mobrand.sdk.core.MobrandCallback;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.event.MobrandLifecycle;
import com.mobrand.sdk.core.model.InterstitialAd;

import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by rmateus on 13/02/16.
 */
public class MobrandSimplyRedInterstitial extends AppCompatActivity {

    private String mPlacementId;
    private TextView loadingText;
    private ProgressBar progressBar;
    private MobrandCore mMobrandCore;
    private RatingBar mRatingBar;

    public static Builder build(Context context, String mPlacementId){
        return new Builder(context, mPlacementId);
    }

    public static class Builder{

        private Context context;
        private String placement;
        private MobrandLifecycle lifecycle;

        public Builder(Context context, String placement){
            this.context = context;
            this.placement = placement;
        }

        public Builder setLifecycle(MobrandLifecycle lifecycle) {
            this.lifecycle = lifecycle;
            return this;
        }

        public void start(){
            MobrandSimplyRedInterstitial.start(context, placement, lifecycle);
        }

    }

    public static void start(Activity activity, String placement) {
        start(activity, placement);
    }

    public static void start(Context context, String placement) {
        start(context, placement, null);
    }

    public static void start(Context context, String placement, MobrandLifecycle callback) {
        Intent intent = new Intent(context, MobrandSimplyRedInterstitial.class);
        intent.putExtra("placementid", placement);
        context.startActivity(intent);
        if (callback != null) {
            EventBus.getDefault().register(callback);
        }
    }

    private View mPleaseWaitView;
    private View mContent;
    private TextView mAppName;
    private TextView mCategory;
    private TextView mDescription;
    private ImageView mAppIconFull;
    private ImageView mAppIcon;
    private Button mInstall;



    @Override
    protected void onStart() {
        super.onStart();

        mPleaseWaitView = findViewById(R.id.pleaseWait);
        mContent = findViewById(R.id.content);
        mAppName = (TextView) findViewById(R.id.appName);
        mCategory = (TextView) findViewById(R.id.appCategory);
        mDescription = (TextView) findViewById(R.id.description);
        mAppIconFull = (ImageView) findViewById(R.id.appIconFull);
        mAppIcon = (ImageView) findViewById(R.id.appIcon);
        mInstall = (Button) findViewById(R.id.install);

        mContent.setVisibility(View.GONE);
        mPleaseWaitView.setVisibility(View.VISIBLE);
        loadingText = (TextView) mPleaseWaitView.findViewById(R.id.loadingText);

        progressBar = (ProgressBar) mPleaseWaitView.findViewById(R.id.progressBar);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mMobrandCore = new MobrandCore(this);



        mMobrandCore.create(new MobrandCallback() {

            @Override
            public void onReady(final MobrandCore mobrandCore) {

                mobrandCore.getAdAsync(mPlacementId, new GetAdCallback() {
                    @Override
                    public void onSuccess(final InterstitialAd ad) {
                        EventBus.getDefault().post(new MobrandLifecycle.Event(MobrandLifecycle.EnumLifecycle.ADSRECEIVED));
                        fadeIn(mContent);
                        fadeOut(mPleaseWaitView);

                        mAppName.setText(ad.getName());
                        mCategory.setText(ad.getCategoryName());
                        mRatingBar.setRating(ad.getName().length() % 2 + 4);

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {

                            Glide.with(MobrandSimplyRedInterstitial.this)
                                    .load(ad.getIcon())
                                    .into(mAppIcon);

                            Glide.with(MobrandSimplyRedInterstitial.this)
                                    .load(ad.getIcon())
                                    .into(mAppIconFull);

                        }

                        mobrandCore.addImpression(ad.getAdid(), mPlacementId);
                        mInstall.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                fadeIn(mPleaseWaitView);

                                loadingText.setText(Html.fromHtml("Going to Google Play Store&#8230;"));

                                mobrandCore.click(ad.getAdid(), mPlacementId, ad.getPackageName(), 0, new ClickCallback() {
                                    @Override
                                    public void onReady() {
                                        finish();
                                    }

                                    @Override
                                    public void onError(String s) {
                                        finish();
                                        MobrandCore.startMarketFromPackage(MobrandSimplyRedInterstitial.this, ad.getPackageName());
                                    }
                                });
                            }
                        });

                        String interstitial = ad.getDescription();
                        mDescription.setText(Html.fromHtml(interstitial).toString());
                    }

                    @Override
                    public void onFailure() {
                        progressBar.setVisibility(View.INVISIBLE);
                        loadingText.setText("There was an error. Please try again.");
                    }
                });
            }

            @Override
            public void onError() {
                progressBar.setVisibility(View.INVISIBLE);
                loadingText.setText("There was an error. Please try again.");
            }
        });
    }


    @Override
    protected void onDestroy() {
        mMobrandCore.destroy();
        if(isFinishing()) {
            EventBus.getDefault().post(new MobrandLifecycle.Event(MobrandLifecycle.EnumLifecycle.DESTROY));
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mb_simplyred);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        init(savedInstanceState);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.mb_interstitial_color, typedValue, true);
        int color = typedValue.data;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }




    }

    private void init(Bundle savedInstanceState) {

        if (savedInstanceState == null) {

            mPlacementId = getIntent().getStringExtra("placementid");

            EventBus.getDefault().post(new MobrandLifecycle.Event(MobrandLifecycle.EnumLifecycle.CREATE));

        } else {
            mPlacementId = savedInstanceState.getString("placementid");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("placementid", mPlacementId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.close) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void fadeIn(View view) {
        Animation fadein = AnimationUtils.loadAnimation(MobrandSimplyRedInterstitial.this, android.R.anim.fade_in);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(fadein);
    }

    public void fadeOut(View view) {
        Animation fadeout = AnimationUtils.loadAnimation(MobrandSimplyRedInterstitial.this, android.R.anim.fade_out);
        view.setVisibility(View.GONE);
        view.startAnimation(fadeout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
