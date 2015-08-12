package com.mobrand.json.model;

/**
 * Created by rmateus on 07/08/15.
 */
public class Impression {

    private String adid;
    private String appid;
    private String placementid;
    private String advid;
    private String androidid;

    public Impression(String adid, String appid, String placement, String advid, String androidid) {
        this.adid = adid;
        this.appid = appid;
        this.placementid = placement;
        this.advid = advid;
        this.androidid = androidid;
    }



    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPlacementid() {
        return placementid;
    }

    public void setPlacementid(String placementid) {
        this.placementid = placementid;
    }

    public String getAdvid() {
        return advid;
    }

    public void setAdvid(String advid) {
        this.advid = advid;
    }

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }
}
