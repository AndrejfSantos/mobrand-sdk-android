package com.mobrand.appwall.view.model;

import android.support.v7.widget.RecyclerView;

import com.mobrand.appwall.adapters.AppwallAdapter;

/**
 * Created by rmateus on 11/08/15.
 */
public class Mobrando extends MobrandType {


    @Override
    public ViewType getViewType() {
        return ViewType.MOBRANDO;
    }

    @Override
    public void onBindViewHolder(AppwallAdapter appwallAdapter, RecyclerView.ViewHolder holder, int position) {

    }

}
