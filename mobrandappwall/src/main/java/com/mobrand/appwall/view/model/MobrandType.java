package com.mobrand.appwall.view.model;

import android.support.v7.widget.RecyclerView;

import com.mobrand.appwall.adapters.AppwallAdapter;

/**
 * Created by rmateus on 06/08/15.
 */
public abstract class MobrandType {

    protected boolean loading;

    public abstract ViewType getViewType();

    public abstract void onBindViewHolder(AppwallAdapter appwallAdapter, RecyclerView.ViewHolder holder, int position);

    public  boolean isLoading(){
        return loading;
    }

    public void setLoading(boolean loading){
        this.loading = loading;
    }
}
