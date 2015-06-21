package com.mobrand.mobrandsample;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.mobrand.json.model.Ads;
import com.mobrand.json.model.IAds;
import com.mobrand.mobrandsample.us.R;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ValueAnimator;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Query;


public class MainActivity extends AppCompatActivity {


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

    public interface Webservice {
        @GET("/ads")
        public void getAds(@Query("country") String country, Callback<List<Ads>> callback);

        @GET("/ads")
        public void getAds(@Query("country") String country, @Query("category") String category, Callback<List<Ads>> callback);
    }

    public static class Response {
        public List<String> list;
    }


    public static abstract class AdsJsonWrapper extends AdsJson {

        private final AdsJson json;

        protected AdsJsonWrapper(AdsJson json) {
            this.json = json;
        }

        @Override
        public String getName() {
            return json.getName();
        }

        @Override
        public String getIcon_url() {
            return json.getIcon_url();
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

    public static class Video implements IAds{

        private final String name;

        public Video(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getIcon_url() {
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

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getStore_url() {
            return null;
        }
    }

    public static class Header implements IAds {

        public Header(String name) {
            this.name = name;
        }

        public String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getIcon_url() {
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

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getStore_url() {
            return null;
        }


    }

    public static class BaseFragment extends Fragment {

        ArrayList<IAds> ads = new ArrayList<>();
        ArrayList<IAds> pagerAds = new ArrayList<>();

        private View mHeaderView;
        private GridLayoutManager gridLayoutManager;
        int color = 1;

        BlockingQueue<String> strings = new ArrayBlockingQueue<String>(1000);

        int colorArray[] = new int[]{R.color.mobrand, R.color.green, R.color.orange, R.color.blue};
        private AppwallAdapter appwallAdapter;
        private boolean runAdapter;
        private FrameLayout progressBar;


        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            Log.d("Mobrand", "Colorize");

            if (isVisibleToUser && color != 0) {
                ((MainActivity) getActivity()).colorizeActionBar(getResources().getColor(colorArray[getArguments().getInt("position")]));
            }


            if(isVisibleToUser){

                if(appwallAdapter!=null){
                    appwallAdapter.setCallImpressions(true);
                }else{
                    runAdapter = true;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            while (true){

                                String take = strings.take();
                                System.out.println(take);
                                HttpURLConnection urlConnection = (HttpURLConnection) new URL(take).openConnection();
                                urlConnection.connect();
                                urlConnection.getInputStream().close();

                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

            } else {
                if (appwallAdapter != null) appwallAdapter.setCallImpressions(false);
            }



        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_main, container, false);

            gridLayoutManager = new GridLayoutManager(container.getContext(), 6);

            progressBar = (FrameLayout) v.findViewById(R.id.progressBar2);
            mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
            mRecyclerView.setLayoutManager(gridLayoutManager);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics())));
            Log.d("Mobrand", "OnCreateView");

            return v;
        }

        RecyclerView mRecyclerView;

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("Mobrand", "OnViewCreated");


            int color = getResources().getColor(colorArray[getArguments().getInt("position")]);
            appwallAdapter = new AppwallAdapter(ads, pagerAds, strings, color);
            appwallAdapter.setCallImpressions(runAdapter);
            mRecyclerView.setAdapter(appwallAdapter);



            RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://api.mobrand.net").build();

            Callback<List<Ads>> trending = new Callback<List<Ads>>() {


                @Override
                public void success(List<Ads> adsJson, retrofit.client.Response response) {
                    ads.clear();

                    int i = 0;


                    for (Ads list : adsJson) {

                        ads.add(new Header(list.getName()));


                        if (list.getType().equals("pager")) {

                            ads.add(new Pager());

                            for (com.mobrand.json.model.List list1 : list.getList()) {
                                pagerAds.add(list1);
                            }

                        }else if(list.getType().equals("video")){
                            ads.add(new Video(list.getList().get(0).getName()));
                        } else {

                            for (com.mobrand.json.model.List list1 : list.getList()) {
                                ads.add(list1);
                            }

                        }

                        i++;
                    }

                    ads.add(new MobrandHeader());

                    //ads.add(new Header("Local top"));
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                        @Override
                        public int getSpanSize(int position) {

                            int itemViewType = mRecyclerView.getAdapter().getItemViewType(position);

                            switch (itemViewType) {
                                case PAGER:
                                case HEADER:
                                case MOBRANDO:
                                case VIDEO:
                                    return gridLayoutManager.getSpanCount();

                                default:
                                    return gridLayoutManager.getSpanCount() / 3;
                            }

                        }

                    });

                    //mRecyclerView.scheduleLayoutAnimation();
                    mRecyclerView.scheduleLayoutAnimation();
                    progressBar.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
                    progressBar.setVisibility(View.GONE);
                    appwallAdapter.notifyDataSetChanged();


                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println(error);
                }
            };
            if (getArguments().getInt("position") == 0) {
                adapter.create(Webservice.class).getAds("us", trending);
            } else if (getArguments().getInt("position") == 1) {
                adapter.create(Webservice.class).getAds("us", "games", trending);
            } else if (getArguments().getInt("position") == 2) {
                adapter.create(Webservice.class).getAds("us", "apps", trending);
            } else {
                adapter.create(Webservice.class).getAds("us", "mobrand", trending);

            }

        }

    }

    private static int darkenColor(int color) {
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


        final ViewPager pager = (ViewPager) findViewById(R.id.content);
        final SavingPagerAdapter pagerAdapter = new SavingPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        mHeaderView = findViewById(R.id.header);


        tabBar = (TabBarView) getSupportActionBar().getCustomView();

        tabBar.setOnTabClickedListener(new TabBarView.OnTabClickedListener() {
            @Override
            public void onTabClicked(int index) {
                pager.setCurrentItem(index);
            }
        });

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

    private static final int HEADER = 0;
    private static final int MOBRANDO = 1;
    private static final int PAGER = 2;
    private static final int VIDEO = 3;

    public static class AppwallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int color;
        private boolean isClickable = true;
        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<IAds> ads = new ArrayList<>();
        ArrayList<IAds> pagerAds = new ArrayList<>();
        private BlockingQueue<String> urls;
        private Object mutex = new Object();

        boolean touching = false;
        private boolean callimpressions;

        public AppwallAdapter(ArrayList<IAds> ads, ArrayList<IAds> pagerAds, BlockingQueue<String> urls, int color) {
            this.ads = ads;
            this.pagerAds = pagerAds;
            this.urls = urls;
            this.color = color;
        }

        @Override
        public int getItemViewType(int position) {


            if (ads.get(position) instanceof Header) {
                return HEADER;
            } else if (ads.get(position) instanceof Pager) {
                return PAGER;
            } else if (ads.get(position) instanceof MobrandHeader) {
                return MOBRANDO;
            } else if (ads.get(position) instanceof Video) {
                return VIDEO;
            } else {
                return 10;
            }
        }

        public void setCallImpressions(boolean callImpressions){
            this.callimpressions = callImpressions;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View inflate;

            switch (viewType) {

                case PAGER:
                    final ViewPager pager = (ViewPager) LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager, parent, false);
                    pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, parent.getResources().getDisplayMetrics()));
                    pager.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            switch (event.getAction()) {

                                case MotionEvent.ACTION_MOVE:
                                case MotionEvent.ACTION_SCROLL:
                                case MotionEvent.ACTION_DOWN:
                                    touching = true;
                                    break;
                                case MotionEvent.ACTION_UP:
                                    touching = false;

                                    break;

                            }

                            System.out.println(touching);

                            return false;
                        }
                    });
                    handler.postDelayed(new Runnable() {

                        int i = 0;

                        @Override
                        public void run() {
                            if (pager != null && !touching) {

                                pager.setCurrentItem(i++ % pager.getAdapter().getCount(), true);
                                com.mobrand.json.model.List iAds = (com.mobrand.json.model.List) pagerAds.get(pager.getCurrentItem());
                                if(callimpressions && !iAds.alreadyImpressed){
                                    String adid = iAds.getAdid();
                                    ((com.mobrand.json.model.List)pagerAds.get(pager.getCurrentItem())).alreadyImpressed = true;
                                    urls.add("http://api.mobrand.net/impressions?appid=123&adid="+adid+"&placementid=App%20Wall");
                                }

                            }

                            handler.postDelayed(this, 3000);

                        }
                    }, 3000);

                    pager.setAdapter(new PagerAdapter() {



                        @Override
                        public Object instantiateItem(ViewGroup container, int position) {

                            final IAds iAds = pagerAds.get(position);
                            View inflate1 = LayoutInflater.from(container.getContext()).inflate(R.layout.big_item, container, false);

                            //View inflate1 = new TextView(container.getContext());
                            //((TextView)inflate1).setText(pagerAds.get(position).getName());
                            container.addView(inflate1);

                            ImageView appIcon = (ImageView) inflate1.findViewById(R.id.appicon);
                            TextView appName = (TextView) inflate1.findViewById(R.id.name);
                            TextView description = (TextView) inflate1.findViewById(R.id.description);

                            appName.setText(iAds.getName());
                            description.setText(Html.fromHtml(iAds.getDescription()).toString());
                            final SmoothProgressBar viewById = (SmoothProgressBar) inflate1.findViewById(R.id.progressBar);
                            viewById.setSmoothProgressDrawableColor(color);
                            Glide.with(parent.getContext())
                                    .load(iAds.getIcon_url())
                                    .fitCenter()
                                    .placeholder(R.color.Transparent)
                                    .into(appIcon);

                            inflate1.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {


                                    switch (event.getAction()) {

                                        case MotionEvent.ACTION_MOVE:
                                        case MotionEvent.ACTION_SCROLL:
                                        case MotionEvent.ACTION_DOWN:
                                            touching = true;
                                            break;
                                        case MotionEvent.ACTION_CANCEL:
                                        case MotionEvent.ACTION_UP:
                                            touching = false;

                                            break;

                                    }

                                    System.out.println(touching);


                                    return false;
                                }
                            });

                            inflate1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    touching = true;
                                    if (isClickable) {
                                        isClickable = false;
                                        viewById.setVisibility(View.VISIBLE);
                                        viewById.progressiveStart();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final String adid = iAds.getAdid();
                                                AdvertisingIdClient.Info advertisingIdInfo;


                                                try {
                                                    advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(v.getContext());
                                                    final String advid = advertisingIdInfo.getId();

                                                    Handler handler = new Handler(Looper.getMainLooper());

                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            synchronized (mutex) {
                                                                if(!isClickable) {

                                                                    viewById.progressiveStop();
                                                                    viewById.setVisibility(View.GONE);
                                                                    isClickable = true;
                                                                    touching = false;
                                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                    intent.setPackage("com.android.vending");
                                                                    //Toast.makeText(v.getContext(), , Toast.LENGTH_LONG).show();
                                                                    intent.setData(Uri.parse(iAds.getStore_url()));
                                                                    v.getContext().startActivity(intent);

                                                                }
                                                            }
                                                        }
                                                    }, 5000);
                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            WebViewClient client = new WebViewClient() {

                                                                @Override
                                                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                                                    System.out.println(url);
                                                                    if (url.contains("market://") || url.contains("https://play.google.com")) {


                                                                        synchronized (mutex){
                                                                            if(!isClickable) {
                                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                                intent.setPackage("com.android.vending");
                                                                                intent.setData(Uri.parse(url));
                                                                                v.getContext().startActivity(intent);
                                                                                viewById.setVisibility(View.GONE);
                                                                                viewById.progressiveStop();
                                                                                isClickable = true;
                                                                                touching = false;
                                                                            }
                                                                        }

                                                                        return false;
                                                                    }

                                                                    return super.shouldOverrideUrlLoading(view, url);
                                                                }
                                                            };

                                                            WebView view = new WebView(v.getContext());
                                                            view.setWebViewClient(client);
                                                            view.getSettings().setJavaScriptEnabled(true);
                                                            String android_id = Settings.Secure.getString(Mobrand.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                                            String url = "http://api.mobrand.net/click?appid=123&placementid=App%20Wall&advid=" + advid + "&androidid=" + android_id + "&adid=" + adid;
                                                            System.out.println(url);
                                                            view.loadUrl(url);


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
                                }
                            });


                            return inflate1;
                        }

                        @Override
                        public void destroyItem(ViewGroup container, int position, Object object) {
                            container.removeView((View) object);
                        }

                        @Override
                        public int getCount() {
                            return pagerAds.size();
                        }

                        @Override
                        public boolean isViewFromObject(View view, Object object) {
                            return view.equals(object);
                        }
                    });

                    return new RecyclerView.ViewHolder(pager) {
                    };
                case HEADER:
                    inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
                    break;
                default:
                    inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_item, parent, false);
                    break;
                case MOBRANDO:
                    return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.mobrando, parent, false)) {
                    };
                case VIDEO:
                    View inflate1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.video, parent, false);
                    VideoView2 videoView = (VideoView2) inflate1.findViewById(R.id.video);
                    //videoView.getLayoutParams().height = (int) (250 * 1.5);


                    videoView.setVideoURI(Uri.parse("android.resource://" + videoView.getContext().getPackageName() + "/" + R.raw.taichi));
                    videoView.start();

                    return new RecyclerView.ViewHolder(inflate1) {};




            }


            return new AppwallViewHolder(inflate);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {


            VideoView2 viewById1 = (VideoView2) holder1.itemView.findViewById(R.id.video);

            if(viewById1!=null){
                ((TextView)holder1.itemView.findViewById(R.id.title)).setText(ads.get(position).getName());
                holder1.itemView.findViewById(R.id.playnow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setPackage("com.android.vending");
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.snailgameusa.tp"));
                        v.getContext().startActivity(intent);
                    }
                });
                viewById1.start();
            }


            if (holder1 instanceof AppwallViewHolder) {

                final AppwallViewHolder holder = (AppwallViewHolder) holder1;
                int itemViewType = getItemViewType(position);
                FrameLayout bottomBar = holder.bottomBar;

                if (itemViewType == HEADER) {

                    holder.name.setText(ads.get(position).getName());

                    return;
                }

                final IAds adsJson = ads.get(position);

                boolean alreadyImpressed = ((com.mobrand.json.model.List) adsJson).alreadyImpressed;

                if(!alreadyImpressed){
                    urls.add("http://api.mobrand.net/impressions?appid=123&adid="+adsJson.getAdid()+"&placementid=App%20Wall");
                    ((com.mobrand.json.model.List) adsJson).alreadyImpressed = true;
                }




                holder.name.setText(adsJson.getName());

                //holder.rating.setRating(adsJson.getRating().floatValue());
                String icon_url = adsJson.getIcon_url();

                RequestListener<String, GlideDrawable> glideDrawableRequestListener = new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bitmap = ((GlideBitmapDrawable) resource).getBitmap();
                        if (bitmap != null) {
                            Context context = holder.itemView.getContext();

//                            PaletteLoader.with(context, model)
//                                    .load(bitmap)
//                                    .setPaletteRequest(new PaletteRequest(PaletteRequest.SwatchType.LIGHT_MUTED, PaletteRequest.SwatchColor.BACKGROUND))
//                                    .into(holder.bottomBar);
//                            PaletteLoader.with(context, model)
//                                    .load(bitmap)
//                                    .setPaletteRequest(new PaletteRequest(PaletteRequest.SwatchType.LIGHT_MUTED, PaletteRequest.SwatchColor.TEXT_TITLE))
//                                    .into(holder.name);

                        }
                        return false;
                    }
                };

                Glide.with(holder.itemView.getContext())
                        .load(icon_url)
                        .fitCenter()
                        .placeholder(R.color.Transparent)
                        .listener(glideDrawableRequestListener)
                        .into(holder.appicon);

                final SmoothProgressBar viewById = (SmoothProgressBar) holder.itemView.findViewById(R.id.progressBar);
                viewById.setSmoothProgressDrawableInterpolator(new AccelerateDecelerateInterpolator());
                viewById.setSmoothProgressDrawableColor(color);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (isClickable) {
                            isClickable = false;
                            viewById.setVisibility(View.VISIBLE);
                            viewById.progressiveStart();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final String adid = adsJson.getAdid();
                                    AdvertisingIdClient.Info advertisingIdInfo;


                                    try {
                                        advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(v.getContext());
                                        final String advid = advertisingIdInfo.getId();

                                        Handler handler = new Handler(Looper.getMainLooper());

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                viewById.progressiveStop();
                                                viewById.setVisibility(View.GONE);

                                                synchronized (mutex) {

                                                    if(!isClickable) {
                                                        isClickable = true;


                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setPackage("com.android.vending");
                                                        //Toast.makeText(v.getContext(), , Toast.LENGTH_LONG).show();
                                                        intent.setData(Uri.parse(adsJson.getStore_url()));
                                                        v.getContext().startActivity(intent);
                                                    }


                                                }
                                            }
                                        }, 5000);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                WebViewClient client = new WebViewClient() {

                                                    @Override
                                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                                        System.out.println(url);
                                                        if (url.contains("market://") || url.contains("https://play.google.com")) {

                                                            synchronized (mutex) {

                                                                if (!isClickable) {
                                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                    intent.setPackage("com.android.vending");
                                                                    intent.setData(Uri.parse(url));
                                                                    v.getContext().startActivity(intent);
                                                                    viewById.setVisibility(View.GONE);
                                                                    viewById.progressiveStop();
                                                                    isClickable = true;
                                                                }

                                                                return false;
                                                            }
                                                        }

                                                        return super.shouldOverrideUrlLoading(view, url);
                                                    }
                                                };

                                                WebView view = new WebView(v.getContext());
                                                view.setWebViewClient(client);
                                                view.getSettings().setJavaScriptEnabled(true);
                                                String android_id = Settings.Secure.getString(Mobrand.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                                                String url = "http://api.mobrand.net/click?appid=123&placementid=App%20Wall&advid=" + advid + "&androidid=" + android_id + "&adid=" + adid;
                                                System.out.println(url);
                                                view.loadUrl(url);

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
                    }
                });

                //Glide.with(holder.itemView.getContext()).load(icon_url).into(holder.appicon);

            }

        }

        @Override
        public int getItemCount() {
            return ads.size();
        }

        public static class AppwallViewHolder extends RecyclerView.ViewHolder {


            public TextView name;
            public ImageView appicon;
            public RatingBar rating;
            public FrameLayout bottomBar;

            public AppwallViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.name);
                appicon = (ImageView) itemView.findViewById(R.id.appicon);
                //bottomBar = (FrameLayout) itemView.findViewById(R.id.bottomBar);

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

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private static class FixedHeader implements IAds {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getIcon_url() {
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

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getStore_url() {
            return null;
        }
    }

    private static class MobrandHeader implements IAds {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getIcon_url() {
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

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getStore_url() {
            return null;
        }
    }

    private static class Pager implements IAds {


        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getIcon_url() {
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

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String getStore_url() {
            return null;
        }
    }

}
