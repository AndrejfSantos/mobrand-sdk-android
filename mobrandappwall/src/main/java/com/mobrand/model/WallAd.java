package com.mobrand.model;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bumptech.glide.Glide;
import com.mobrand.appwall.classic.R;
import com.mobrand.sdk.core.ClickCallback;
import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.sdk.core.model.CategoryEnum;
import com.mobrand.appwall.adapters.AppwallAdapter;
import com.mobrand.appwall.view.model.MobrandType;
import com.mobrand.appwall.view.model.ViewType;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by rmateus on 02/12/15.
 */
public class WallAd extends MobrandType {

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
    public void onBindViewHolder(final AppwallAdapter appwallAdapter, RecyclerView.ViewHolder holderArg, final int position) {
        final MobrandCore core = appwallAdapter.getMobrandCore();

        if(!impressed && appwallAdapter.isCallimpressions()){
           core.addImpression(adid, appwallAdapter.getPlacementId());
           this.impressed = true;
        }

        AppwallAdapter.AppwallViewHolder holder = (AppwallAdapter.AppwallViewHolder) holderArg;

        final Context context = holder.itemView.getContext();
        holder.name.setText(name);

        Glide.with(context)
                .load(icon)
                .fitCenter()
                .placeholder(R.color.Transparent)
                .into(holder.appicon);

        final SmoothProgressBar smoothProgressBar = holder.progressBar;


        smoothProgressBar.setProgressiveStartActivated(!isLoading());

        if(isLoading()){
            smoothProgressBar.setVisibility(View.VISIBLE);
        }else{
            smoothProgressBar.setVisibility(View.GONE);
        }

        smoothProgressBar.setSmoothProgressDrawableInterpolator(new AccelerateDecelerateInterpolator());
        smoothProgressBar.setSmoothProgressDrawableColor(appwallAdapter.getColor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (appwallAdapter.isClickable()) {
                    setLoading(true);
                    appwallAdapter.setIsClickable(false);
                    smoothProgressBar.setVisibility(View.VISIBLE);
                    smoothProgressBar.progressiveStart();

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            core.click(adid, appwallAdapter.getPlacementId(),packageName, position, new ClickCallback() {

                                @Override
                                public void onReady() {
                                    setLoading(false);

                                    appwallAdapter.setIsClickable(true);
                                    smoothProgressBar.progressiveStop();
                                    smoothProgressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(String message) {
                                    onReady();
                                    MobrandCore.startMarketFromPackage(context, packageName);
                                }
                            });

                        }
                    });

                }
            }
        });

    }


}
