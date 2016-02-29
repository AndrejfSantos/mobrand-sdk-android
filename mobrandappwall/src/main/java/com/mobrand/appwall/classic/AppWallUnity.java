package com.mobrand.appwall.classic;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobrand.appwall.adapters.SavingPagerAdapter;
import com.mobrand.appwall.event.NewAdsEvent;
import com.mobrand.appwall.view.TabBarView;
import com.mobrand.appwall.view.TabView;
import com.mobrand.model.DefaultGroups;
import com.mobrand.model.GroupStringsMapJson;
import com.mobrand.model.Webservices;
import com.mobrand.sdk.core.Defaults;
import com.mobrand.sdk.core.GetAdsCallback;
import com.mobrand.sdk.core.MobrandCallback;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.CategoryEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.mobrand.model.Constants.APPS;
import static com.mobrand.model.Constants.GAMES;
import static com.mobrand.model.Constants.GENERIC;
import static com.mobrand.model.Constants.MOBRAND;


/**
 * Created by rmateus on 02/12/15.
 */
public class AppWallUnity extends AppCompatActivity {


    public static void startAppWall(Activity activity, String placement, boolean asInterstitial){
        AppWall.start(activity, placement, null, asInterstitial);
    }

}
