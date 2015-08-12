package com.mobrand.json.model;

/**
 * Created by rmateus on 11/08/15.
 */
public class Config {


    private String adsEndpoint;
    private String clicksEndpoint;
    private String impressionsEndpoint;
    private boolean ads;

    public String getAdsEndpoint() {
        return adsEndpoint;
    }

    public void setAdsEndpoint(String adsEndpoint) {
        this.adsEndpoint = adsEndpoint;
    }

    public String getClicksEndpoint() {
        return clicksEndpoint;
    }

    public void setClicksEndpoint(String clicksEndpoint) {
        this.clicksEndpoint = clicksEndpoint;
    }

    public String getImpressionsEndpoint() {
        return impressionsEndpoint;
    }

    public void setImpressionsEndpoint(String impressionsEndpoint) {
        this.impressionsEndpoint = impressionsEndpoint;
    }

    public boolean isAds() {
        return ads;
    }

    public void setAds(boolean ads) {
        this.ads = ads;
    }
}
