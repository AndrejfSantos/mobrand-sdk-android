package com.mobrand.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.mobrand.json.model.IAds;
import com.mobrand.json.model.Impression;
import com.mobrand.appwall.ImpressionsEngine;
import com.mobrand.model.Header;
import com.mobrand.model.MobrandType;
import com.mobrand.model.ViewType;
import com.mobrand.sdk.R;

import java.io.IOException;
import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by rmateus on 11/08/15.
 */
public class AppwallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int color;
    private final String appid;
    private final String placement;
    private boolean isClickable = true;
    Handler handler = new Handler(Looper.getMainLooper());
    ArrayList<MobrandType> ads = new ArrayList<>();
    ArrayList<IAds> pagerAds = new ArrayList<>();
    private final Object mutex = new Object();

    private String clicksEndpoint = "http://api.mobrand.net/";


    boolean touching = false;
    private boolean callimpressions;

    public AppwallAdapter(ArrayList<MobrandType> ads, ArrayList<IAds> pagerAds,  int color, String appId, String placementId) {

        this.ads = ads;
        this.pagerAds = pagerAds;
        this.color = color;
        this.appid = appId;
        this.placement = placementId;

    }

    @Override
    public int getItemViewType(int position) {
        return ads.get(position).getViewType().ordinal();
    }

    public void setCallImpressions(boolean callImpressions){
        this.callimpressions = callImpressions;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int type) {

        View inflate;
        ViewType viewType = ViewType.values()[type];


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

                                Impression impression = new Impression(adid, appid, placement, "", "");
                                ImpressionsEngine.addImpression(impression);
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



                                                        WebView view = new WebView(v.getContext());
                                                        view.setWebViewClient(new MobrandWebViewClient(v.getContext(), viewById));
                                                        view.getSettings().setJavaScriptEnabled(true);
                                                        String android_id = Settings.Secure.getString(v.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
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


        }


        return new AppwallViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {


        int itemViewType = holder1.getItemViewType();

        ViewType type = ViewType.values()[itemViewType];


        switch (type){

            case HEADER:
                final AppwallViewHolder holder = (AppwallViewHolder) holder1;
                Header ad = (Header) ads.get(position);
                holder.name.setText(ad.getName());
                return;
            case MOBRANDO:
                return;
            case ITEM:
                break;
            case VIDEO:
                return;
            case PAGER:
                return;

        }

        if (holder1 instanceof AppwallViewHolder) {

            final AppwallViewHolder holder = (AppwallViewHolder) holder1;


            final IAds adsJson = (IAds) ads.get(position);

            boolean alreadyImpressed = ((com.mobrand.json.model.List) adsJson).alreadyImpressed;

            if(!alreadyImpressed){

                Impression impression = new Impression(adsJson.getAdid(), appid, placement, "", "");

                ImpressionsEngine.addImpression(impression);

                ((com.mobrand.json.model.List) adsJson).alreadyImpressed = true;
            }

            holder.name.setText(adsJson.getName());
            String icon_url = adsJson.getIcon_url();
            Glide.with(holder.itemView.getContext())
                    .load(icon_url)
                    .fitCenter()
                    .placeholder(R.color.Transparent)
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

                                            WebView view = new WebView(v.getContext());
                                            view.setWebViewClient(new MobrandWebViewClient(v.getContext(), viewById));
                                            view.getSettings().setJavaScriptEnabled(true);
                                            String android_id = Settings.Secure.getString(v.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                                            String url = clicksEndpoint + "click?appid=" + appid + "&placementid=" + placement + "&advid=" + advid + "&androidid=" + android_id + "&adid=" + adid;

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


        }

    }


    public class MobrandWebViewClient extends WebViewClient{

        Context context;
        SmoothProgressBar progressBar;


        public MobrandWebViewClient(Context context, SmoothProgressBar progressBar) {
            this.context = context;
            this.progressBar = progressBar;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("market://") || url.contains("https://play.google.com")) {

                synchronized (mutex) {

                    if (!isClickable) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setPackage("com.android.vending");
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        progressBar.progressiveStop();
                        isClickable = true;

                    }

                    return false;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

    }

    @Override
    public int getItemCount() {
        return ads.size();
    }

    public static class AppwallViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public ImageView appicon;

        public AppwallViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            appicon = (ImageView) itemView.findViewById(R.id.appicon);
        }
    }


}
