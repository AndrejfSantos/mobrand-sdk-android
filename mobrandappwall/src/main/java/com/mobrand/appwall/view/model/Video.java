package com.mobrand.appwall.view.model;

import android.support.v7.widget.RecyclerView;

import com.mobrand.appwall.adapters.AppwallAdapter;

/**
 * Created by rmateus on 11/08/15.
 */
public class Video extends MobrandType {

    private final String name;

    public Video(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public ViewType getViewType() {
        return ViewType.VIDEO;
    }

    @Override
    public void onBindViewHolder(AppwallAdapter appwallAdapter, RecyclerView.ViewHolder holder, int position) {

    }
}