package com.mobrand.view.model;

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
import com.mobrand.appwall.A.R;
import com.mobrand.model.AppWallAd;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.view.AppwallAdapter;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by rmateus on 11/08/15.
 */
public class Pager extends MobrandType {


    List<AppWallAd> pagerAppWallAds = new ArrayList<>();

    @Override
    public ViewType getViewType() {
        return ViewType.PAGER;
    }

    public List<AppWallAd> getPagerAppWallAds() {
        return pagerAppWallAds;
    }

    public void setPagerAppWallAds(List<AppWallAd> pagerAppWallAds) {
        this.pagerAppWallAds = pagerAppWallAds;
    }

    @Override
    public void onBindViewHolder(AppwallAdapter appwallAdapter, MobrandCore core, RecyclerView.ViewHolder holder, int position) {

        ((ViewPager)holder.itemView).setAdapter(new MobrandPagerAdapter(core, pagerAppWallAds, appwallAdapter));

    }

    private static class MobrandPagerAdapter extends PagerAdapter {


        private final MobrandCore core;
        private final String placementId;
        private List<AppWallAd> pagerAppWallAds;
        private AppwallAdapter adapter;
        private int color;
        private boolean touching;

        public MobrandPagerAdapter(MobrandCore core, List<AppWallAd> pagerAppWallAds, AppwallAdapter adapter) {
            this.core = core;
            this.color = adapter.getColor();
            this.placementId = adapter.getPlacementId();
            this.pagerAppWallAds = pagerAppWallAds;
            this.adapter = adapter;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            final AppWallAd iAds = pagerAppWallAds.get(position);
            View inflate1 = LayoutInflater.from(container.getContext()).inflate(R.layout.big_item, container, false);

            //View inflate1 = new TextView(container.getContext());
            //((TextView)inflate1).setText(pagerAppWallAds.get(position).getName());
            container.addView(inflate1);

            ImageView appIcon = (ImageView) inflate1.findViewById(R.id.appicon);
            TextView appName = (TextView) inflate1.findViewById(R.id.name);
            TextView description = (TextView) inflate1.findViewById(R.id.description);

            appName.setText(iAds.getName());
            description.setText(Html.fromHtml(iAds.getDescription()).toString());
            final SmoothProgressBar viewById = (SmoothProgressBar) inflate1.findViewById(R.id.progressBar);
            viewById.setSmoothProgressDrawableColor(color);
            Glide.with(container.getContext())
                    .load(iAds.getIcon())
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

                    return false;
                }
            });

            inflate1.setOnClickListener(new PagerOnClickListener(viewById, iAds, position));


            return inflate1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return pagerAppWallAds.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        private class PagerOnClickListener implements View.OnClickListener {
            private final SmoothProgressBar viewById;
            private final AppWallAd iAds;
            private final int position;

            public PagerOnClickListener(SmoothProgressBar viewById, AppWallAd iAds, int position) {
                this.viewById = viewById;
                this.iAds = iAds;
                this.position = position;
            }

            @Override
            public void onClick(final View v) {


                if (adapter.isClickable()) {

                    adapter.setIsClickable(false);

                    viewById.setVisibility(View.VISIBLE);
                    viewById.progressiveStart();

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (!adapter.isClickable()) {

                                viewById.progressiveStop();
                                viewById.setVisibility(View.GONE);
                                adapter.setIsClickable(true);
                                touching = false;
                                MobrandCore.startMarketFromPackage(viewById.getContext(), iAds.getPackageName());

                            }

                        }
                    }, 5000);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            core.click(iAds.getAdid(), placementId, position, new MobrandCore.ClickCallback() {
                                @Override
                                public void onReady() {

                                }
                            });
                        }
                    });

                }
            }
        }
    }
}