package com.mobrand.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.mobrand.appwall.A.R;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.model.CategoryEnum;
import com.mobrand.view.AppwallAdapter;
import com.mobrand.view.model.MobrandType;
import com.mobrand.view.model.ViewType;

import java.io.IOException;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by rmateus on 02/12/15.
 */
public class AppWallAd extends MobrandType {

    private String name;
    private String description;
    private String icon;
    private CategoryEnum category;
    private String adid;
    private boolean impressed;
    private String packageName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public boolean isImpressed() {
        return impressed;
    }

    public void setImpressed(boolean impressed) {
        this.impressed = impressed;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.ITEM;
    }

    @Override
    public void onBindViewHolder(final AppwallAdapter appwallAdapter, final MobrandCore core, RecyclerView.ViewHolder holderArg, final int position) {

        if(!impressed){
           core.addImpression(adid, appwallAdapter.getPlacementId());
           this.impressed = true;
        }
        AppwallAdapter.AppwallViewHolder holder = (AppwallAdapter.AppwallViewHolder) holderArg;

        final Context context = holder.itemView.getContext();
        holder.name.setText(name);

        System.out.println(icon);

        Glide.with(context)
                .load(icon)
                .fitCenter()
                .placeholder(R.color.Transparent)
                .into(holder.appicon);

        final SmoothProgressBar smoothProgressBar = (SmoothProgressBar) holder.itemView.findViewById(R.id.progressBar);
        smoothProgressBar.setSmoothProgressDrawableInterpolator(new AccelerateDecelerateInterpolator());
        smoothProgressBar.setSmoothProgressDrawableColor(appwallAdapter.getColor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                if (appwallAdapter.isClickable()) {

                    appwallAdapter.setIsClickable(false);
                    smoothProgressBar.setVisibility(View.VISIBLE);
                    smoothProgressBar.progressiveStart();

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            smoothProgressBar.progressiveStop();
                            smoothProgressBar.setVisibility(View.GONE);

                            if (!appwallAdapter.isClickable()) {
                                appwallAdapter.setIsClickable(true);
                                MobrandCore.startMarketFromPackage(context, packageName);
                            }
                        }
                    }, 5000);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            core.click(adid, appwallAdapter.getPlacementId(), position, new MobrandCore.ClickCallback() {
                                @Override
                                public void onReady() {
                                    appwallAdapter.setIsClickable(true);
                                    smoothProgressBar.progressiveStop();
                                    smoothProgressBar.setVisibility(View.GONE);
                                }
                            });

                        }
                    });

                }
            }
        });

    }
}
