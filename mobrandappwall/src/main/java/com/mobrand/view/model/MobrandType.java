package com.mobrand.view.model;

import android.support.v7.widget.RecyclerView;

import com.mobrand.sdk.core.MobrandCore;
import com.mobrand.view.AppwallAdapter;

/**
 * Created by rmateus on 06/08/15.
 */
public abstract class MobrandType {

    public abstract ViewType getViewType();

    public abstract void onBindViewHolder(AppwallAdapter appwallAdapter, MobrandCore core, RecyclerView.ViewHolder holder, int position);
}
