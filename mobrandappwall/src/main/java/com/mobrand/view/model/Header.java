package com.mobrand.view.model;

import android.support.v7.widget.RecyclerView;

import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.view.AppwallAdapter;

/**
 * Created by rmateus on 11/08/15.
 */
public class Header extends MobrandType{

    public Header(String name) {
        this.name = name;
    }

    public String name;

    public String getName() {
        return name;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.HEADER;
    }

    @Override
    public void onBindViewHolder(AppwallAdapter appwallAdapter, MobrandCore core, RecyclerView.ViewHolder holderArg, int position) {

        final AppwallAdapter.AppwallViewHolder holder = (AppwallAdapter.AppwallViewHolder) holderArg;
        holder.name.setText(name);

    }
}
