package com.mobrand.view.model;

import android.support.v7.widget.RecyclerView;

import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.view.AppwallAdapter;

/**
 * Created by rmateus on 11/08/15.
 */
public class Mobrando extends MobrandType {


    @Override
    public ViewType getViewType() {
        return ViewType.MOBRANDO;
    }

    @Override
    public void onBindViewHolder(AppwallAdapter appwallAdapter, MobrandCore core, RecyclerView.ViewHolder holder, int position) {

    }
}
