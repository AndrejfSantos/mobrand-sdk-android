package com.mobrand.appwall.event;

import android.support.v4.util.SparseArrayCompat;

import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.NativeAd;

import java.util.List;

/**
 * Created by rmateus on 02/12/15.
 */
public class NewAdsEvent {

    private SparseArrayCompat<List<String>> groups;
    private List<Ad> list;

    public NewAdsEvent(List<Ad> list, SparseArrayCompat<List<String>> groups) {
        this.list = list;
        this.groups = groups;
    }

    public List<Ad> getList() {
        return list;
    }

    public SparseArrayCompat<List<String>> getHeaders() {
        return groups;
    }
}
