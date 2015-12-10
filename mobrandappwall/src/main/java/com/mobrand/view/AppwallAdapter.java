package com.mobrand.view;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import com.mobrand.appwall.A.R;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.view.model.Header;
import com.mobrand.view.model.MobrandType;
import com.mobrand.view.model.ViewType;

import java.io.IOException;
import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by rmateus on 11/08/15.
 */
public class AppwallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int color;
    private final MobrandCore core;
    private boolean isClickable = true;

    Handler handler = new Handler(Looper.getMainLooper());
    ArrayList<MobrandType> ads = new ArrayList<>();
    private final Object mutex = new Object();

    private String clicksEndpoint = "http://api.mobrand.net/";


    boolean touching = false;
    private boolean callimpressions;
    private String placementId;

    public AppwallAdapter(ArrayList<MobrandType> ads, MobrandCore core,  int color, String placementId) {

        this.ads = ads;
        this.color = color;
        this.core = core;
        this.placementId = placementId;

    }

    @Override
    public int getItemViewType(int position) {
        return ads.get(position).getViewType().ordinal();
    }

    public void setCallImpressions(boolean callImpressions){
        this.callimpressions = callImpressions;
    }

    public void setIsClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int type) {

        View inflate;
        ViewType viewType = ViewType.values()[type];


        switch (viewType) {

            case PAGER:
                final ViewPager pager = (ViewPager) LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager, parent, false);
                pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, parent.getResources().getDisplayMetrics()));
                pager.setOnTouchListener(new PagerOnTouchListener());

//                handler.postDelayed(new Runnable() {
//
//                    int i = 0;
//
//                    @Override
//                    public void run() {
//                        if (pager != null && !touching) {
//
//                            pager.setCurrentItem(i++ % pager.getAdapter().getCount(), true);
//                            com.mobrand.json.model.List iAds = (com.mobrand.json.model.List) pagerAds.get(pager.getCurrentItem());
//                            if(callimpressions && !iAds.alreadyImpressed){
//
//
//                                String adid = iAds.getAdid();
//                                ((com.mobrand.json.model.List)pagerAds.get(pager.getCurrentItem())).alreadyImpressed = true;
//
//                                Impression impression = new Impression(adid, appid, placement, "", "");
//                                ImpressionsEngine.addImpression(impression);
//                            }
//
//                        }
//
//                        handler.postDelayed(this, 3000);
//
//                    }
//                }, 3000);


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

    public boolean isClickable() {
        return isClickable;
    }

    public boolean isCallimpressions() {
        return callimpressions;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {


        ads.get(position).onBindViewHolder(this, core, holder1, position);


    }


    @Override
    public int getItemCount() {
        return ads.size();
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public int getColor() {
        return color;
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


    private class PagerOnTouchListener implements View.OnTouchListener {
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
    }
}
