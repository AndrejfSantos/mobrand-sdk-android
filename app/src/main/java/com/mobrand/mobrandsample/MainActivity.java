package com.mobrand.mobrandsample;


import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewCompatKitKat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.widget.ViewUtils;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.GET;


public class MainActivity extends ActionBarActivity {


    private TabBarView tabBar;
    private View headerView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private int mBaseTranslationY;
    private Toolbar mToolbarView;
    private View mHeaderView;
    private RecyclerView mRecyclerView;

    public View getHeaderView() {
        return mHeaderView;
    }

    public interface Webservice{
        @GET("/ads")
        public void getAds(Callback<List<AdsJson>> callback);
    }

    public static class Response{
        public List<String> list;
    }


    public static abstract class AdsJsonWrapper extends AdsJson{

        private final AdsJson json;

        protected AdsJsonWrapper(AdsJson json) {
            this.json = json;
        }

        @Override
        public String getName() {
            return json.getName();
        }

        @Override
        public String getIcon() {
            return json.getIcon();
        }

        @Override
        public String getAdid() {
            return json.getAdid();
        }

        @Override
        public Number getRating() {
            return json.getRating();
        }
    }

    public static class DailyTop extends AdsJsonWrapper {


        protected DailyTop(AdsJson json) {
            super(json);
        }
    }
    public static class Trending extends AdsJsonWrapper {

        protected Trending(AdsJson json) {
            super(json);
        }
    }
    public static class LocalTop extends AdsJsonWrapper {

        protected LocalTop(AdsJson json) {
            super(json);
        }
    }
    public static class Recommended extends AdsJsonWrapper {

        protected Recommended(AdsJson json) {
            super(json);
        }
    }

    public static class Header implements Ads{

        public Header(String name) {
            this.name = name;
        }

        public String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getAdid() {
            return null;
        }

        @Override
        public Number getRating() {
            return null;
        }


    }

    public static class BaseFragment extends Fragment{

        ArrayList<Ads> ads = new ArrayList<>();
        private View mHeaderView;
        private GridLayoutManager gridLayoutManager;
        int color = 1;

        int colorArray[] = new int[]{R.color.blue, R.color.green, R.color.orange,R.color.mobrand };


        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            Log.d("Mobrand", "Colorize");

            if (isVisibleToUser && color != 0) {
                ((MainActivity) getActivity()).colorizeActionBar(getResources().getColor(colorArray[getArguments().getInt("position")]));
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_main, container, false);

            gridLayoutManager = new GridLayoutManager(container.getContext(), 6);

            mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            Log.d("Mobrand", "OnCreateView");

            return v;
        }
        RecyclerView mRecyclerView;
        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("Mobrand", "OnViewCreated");


            final AppwallAdapter appwallAdapter = new AppwallAdapter(ads);
            mRecyclerView.setAdapter(appwallAdapter);


            RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://api.mobrand.net").build();

            adapter.create(Webservice.class).getAds(new Callback<List<AdsJson>>() {
                @Override
                public void success(List<AdsJson> adsJson, retrofit.client.Response response) {
                    ads.clear();
                    ads.add(new FixedHeader());
                    ads.add(new Header("Top Picks"));
                    int all = 0;
                    for (int i = all; i < 6; i++, all++) {
                        DailyTop dailyTop = new DailyTop(adsJson.get(i));
                        ads.add(dailyTop);
                    }

                    ads.add(new Header("Trending"));


                    for (int i = all; i < 12; i++, all++) {
                        Trending dailyTop = new Trending(adsJson.get(i));
                        ads.add(dailyTop);
                    }

                    //ads.add(new Header("Local top"));
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                        @Override
                        public int getSpanSize(int position) {

                            //if(position>1) {
                            if (ads.get(position) instanceof Header || ads.get(position) instanceof FixedHeader || ads.get(position) instanceof MobrandHeader) {
                                return 6;
                            }

                            if (ads.get(position) instanceof Trending) {
                                return 3;
                            }

                            return 2;
                        }
                        //return 6;

                    });

                    ads.add(new MobrandHeader());
                    //mRecyclerView.scheduleLayoutAnimation();
                    mRecyclerView.scheduleLayoutAnimation();

                    appwallAdapter.notifyDataSetChanged();



                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println(error);
                }
            });

        }





    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main2);
        mToolbarView = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarView);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tabbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);



        ViewPager pager  = (ViewPager) findViewById(R.id.content);
        final SavingPagerAdapter pagerAdapter= new SavingPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        mHeaderView = findViewById(R.id.header);


        tabBar = (TabBarView) getSupportActionBar().getCustomView();


        pager.setOffscreenPageLimit(4);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    public static class AppwallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        ArrayList<Ads> ads = new ArrayList<>();

        public AppwallAdapter(ArrayList<Ads> ads) {
            this.ads = ads;
        }

        @Override
        public int getItemViewType(int position) {


            if(ads.get(position) instanceof Header) {
                return 2;
            }else if(ads.get(position) instanceof FixedHeader){
                return 1;
            }else if(ads.get(position) instanceof MobrandHeader){
                return 3;
            }else{
                return 0;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate;

            if(viewType == 2){
                inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
            }else if(viewType == 0){
                inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_item, parent, false);
            } else if (viewType == 3){
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mobrando, parent, false)){};
            }else{
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fixedheader, parent, false)){};
            }


            return new AppwallViewHolder(inflate);
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {

            if(holder1 instanceof AppwallViewHolder){


            AppwallViewHolder holder = (AppwallViewHolder) holder1;
            int itemViewType = getItemViewType(position);

            if(itemViewType == 2){

                holder.name.setText(ads.get(position).getName());

                return;
            }

            final Ads adsJson = ads.get(position);
            holder.name.setText(adsJson.getName());
            holder.rating.setRating(adsJson.getRating().floatValue());
            String icon_url = adsJson.getIcon();

            if(icon_url.contains("_icon")){
                String[] splittedUrl = icon_url.split("\\.(?=[^\\.]+$)");
                icon_url = splittedUrl[0] + "_" + "144x144" + "."+ splittedUrl[1];
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final View viewById = v.findViewById(R.id.loading);


                    viewById.setVisibility(View.VISIBLE);
                    viewById.startAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String adid = adsJson.getAdid();
                            AdvertisingIdClient.Info advertisingIdInfo;

                            try {
                                advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(v.getContext());
                                final String advid = advertisingIdInfo.getId();

                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        WebViewClient client = new WebViewClient(){

                                            @Override
                                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                                System.out.println(url);
                                                if(url.contains("market://") || url.contains("https://play.google.com")){

                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setPackage("com.android.vending");
                                                    intent.setData(Uri.parse(url));
                                                    v.getContext().startActivity(intent);
                                                    viewById.setVisibility(View.GONE);
                                                    viewById.startAnimation(AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_out));
                                                    return false;
                                                }

                                                return super.shouldOverrideUrlLoading(view, url);
                                            }
                                        };

                                        WebView view = new WebView(v.getContext());
                                        view.setWebViewClient(client);
                                        view.getSettings().setJavaScriptEnabled(true);
                                        view.loadUrl("http://api.mobrand.net/click?appid=123&advid="+advid+"&androidid=2312323&adid="+adid);

                                    }
                            });

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesNotAvailableException e) {
                                e.printStackTrace();
                            } catch (GooglePlayServicesRepairableException e) {
                                e.printStackTrace();
                            }


                        }
                    }).start();







                }
            });

            Picasso.with(holder.itemView.getContext()).load(icon_url).into(holder.appicon);

            }

        }

        @Override
        public int getItemCount() {
            return ads.size();
        }

        public static class AppwallViewHolder extends RecyclerView.ViewHolder{


            public TextView name;
            public ImageView appicon;
            public RatingBar rating;

            public AppwallViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                appicon = (ImageView) itemView.findViewById(R.id.appicon);
                rating = (RatingBar) itemView.findViewById(R.id.rating);
            }
        }


    }

    public Toolbar getToolbar() {
        return mToolbarView;
    }

    public void colorizeActionBar(int color) {
        int oldColor = getResources().getColor(R.color.red);
        if (getToolbar() != null) {
            Drawable toolbarDrawable = getToolbar().getBackground();
            if (toolbarDrawable != null && toolbarDrawable instanceof ColorDrawable) {
                oldColor = ((ColorDrawable) toolbarDrawable).getColor();
            }

        }
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), oldColor, color);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int color = (Integer) valueAnimator.getAnimatedValue();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(darkenColor(color));
                }
                getToolbar().setBackgroundColor(color);
            }
        });
        colorAnimation.setDuration(300);
        colorAnimation.start();
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class FixedHeader implements Ads {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getAdid() {
            return null;
        }

        @Override
        public Number getRating() {
            return null;
        }
    }

    private static class MobrandHeader implements Ads {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getIcon() {
            return null;
        }

        @Override
        public String getAdid() {
            return null;
        }

        @Override
        public Number getRating() {
            return null;
        }
    }
}
