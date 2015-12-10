package com.mobrand.sdk.core.model;

import java.util.List;

/**
 * Created by rmateus on 07/08/15.
 */
public class Impression {


    private String appId;
    private String placementId;
    private String androidId;
    private String advId;
    private List<String> impressions;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public List<String> getImpressions() {
        return impressions;
    }

    public void setImpressions(List<String> impressions) {
        this.impressions = impressions;
    }

    @Override
    public String toString() {
        return "Impression{" +
                "appId='" + appId + '\'' +
                ", placementId='" + placementId + '\'' +
                ", androidId='" + androidId + '\'' +
                ", advId='" + advId + '\'' +
                ", impressions=" + impressions +
                '}';
    }
}
