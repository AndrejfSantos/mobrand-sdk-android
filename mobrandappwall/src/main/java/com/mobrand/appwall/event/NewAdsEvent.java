package com.mobrand.appwall.event;

import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.NativeAd;

import java.util.List;

/**
 * Created by rmateus on 02/12/15.
 */
public class NewAdsEvent {

    private List<Ad> list;

    public NewAdsEvent(List<Ad> list) {
        this.list = list;
    }

    public List<Ad> getList() {
        return list;
    }
}
