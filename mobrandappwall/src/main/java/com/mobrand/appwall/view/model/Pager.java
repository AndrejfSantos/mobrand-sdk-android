package com.mobrand.appwall.view.model;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobrand.appwall.classic.R;
import com.mobrand.appwall.adapters.AppwallAdapter;
import com.mobrand.model.WallAd;
import com.mobrand.sdk.core.ClickCallback;
import com.mobrand.sdk.core.MobrandCore;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by rmateus on 11/08/15.
 */
public class Pager extends MobrandType {

    List<MobrandType> pagerAppWallAds = new ArrayList<>();

    public Pager(List<MobrandType> pagerAppWallAds) {
        this.pagerAppWallAds = pagerAppWallAds;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.PAGER;
    }

    @Override
    public void onBindViewHolder(AppwallAdapter appwallAdapter, RecyclerView.ViewHolder holder, int position) {

        ViewPager itemView = (ViewPager) holder.itemView;

        if(itemView.getAdapter()==null) {
            itemView.setAdapter(new MobrandPagerAdapter(pagerAppWallAds, appwallAdapter));
            itemView.setCurrentItem(Integer.MAX_VALUE /2);
        }

    }

    public static class MobrandPagerAdapter extends PagerAdapter {


        private final MobrandCore core;
        private final String placementId;
        private List<MobrandType> pagerAppWallAds;
        private AppwallAdapter adapter;
        private int color;
        private boolean touching;

        public boolean isTouching() {
            return touching;
        }

        public MobrandPagerAdapter(List<MobrandType> pagerAppWallAds, AppwallAdapter adapter) {
            this.core = adapter.getMobrandCore();
            this.color = adapter.getColor();
            this.placementId = adapter.getPlacementId();
            this.pagerAppWallAds = pagerAppWallAds;
            this.adapter = adapter;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {


            final WallAd iAds = (WallAd) pagerAppWallAds.get(position % pagerAppWallAds.size());

            final MobrandCore core = adapter.getMobrandCore();

            if(!iAds.isImpressed() && adapter.isCallimpressions()){
                core.addImpression(iAds.getAdid(), adapter.getPlacementId());
                iAds.setImpressed(true);
            }

            View inflate = LayoutInflater.from(container.getContext()).inflate(R.layout.mb_big_item, container, false);

            inflate.setOnTouchListener(new View.OnTouchListener() {
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
                    return false;
                }
            });

            container.addView(inflate);

            ImageView appIcon = (ImageView) inflate.findViewById(R.id.appicon);
            TextView appName = (TextView) inflate.findViewById(R.id.name);
            TextView description = (TextView) inflate.findViewById(R.id.description);

            appName.setText(iAds.getName());
            description.setText(Html.fromHtml(iAds.getDescription()).toString());
            final SmoothProgressBar progressBar = (SmoothProgressBar) inflate.findViewById(R.id.progressBar);

            if(iAds.isLoading()){
                progressBar.setVisibility(View.VISIBLE);
            }else{
                progressBar.setVisibility(View.GONE);
            }

            progressBar.setSmoothProgressDrawableColor(color);

            Glide.with(container.getContext())
                    .load(iAds.getIcon())
                    .fitCenter()

                    .placeholder(R.color.Transparent)
                    .into(appIcon);

            inflate.setOnClickListener(new PagerOnClickListener( progressBar, iAds, position));


            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return pagerAppWallAds.isEmpty() ?
                    0
                    : pagerAppWallAds.size() > 1 ?
                        Integer.MAX_VALUE : 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        private class PagerOnClickListener implements View.OnClickListener {
            private final SmoothProgressBar progressBar;
            private final WallAd iAds;
            private final int position;

            public PagerOnClickListener( SmoothProgressBar progressBar, WallAd iAds, int position) {
                this.progressBar = progressBar;
                this.iAds = iAds;
                this.position = position;
            }

            @Override
            public void onClick(final View v) {


                if (adapter.isClickable()) {

                    touching = true;
                    iAds.setLoading(true);
                    adapter.setIsClickable(false);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.progressiveStart();

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            core.click(iAds.getAdid(), placementId,iAds.getPackageName(), position, new ClickCallback() {
                                @Override
                                public void onReady() {
                                    iAds.setLoading(false);
                                    adapter.setIsClickable(true);
                                    progressBar.progressiveStop();
                                    progressBar.setVisibility(View.GONE);

                                    touching = false;
                                }

                                @Override
                                public void onError(String message) {
                                    System.out.println(message);
                                    onReady();
                                    touching = false;
                                    MobrandCore.startMarketFromPackage(progressBar.getContext(), iAds.getPackageName());
                                }
                            });
                        }
                    });

                }
            }
        }
    }
}