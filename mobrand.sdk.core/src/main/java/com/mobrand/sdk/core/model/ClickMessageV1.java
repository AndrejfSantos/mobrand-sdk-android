package com.mobrand.sdk.core.model;

/**
 * Created by rmateus on 04/01/16.
 */
public class ClickMessageV1 {

    private String androidid;
    private String identifier;
    private String advid;
    private String placementid;

    public ClickMessageV1(){}
    public ClickMessageV1(String androidid, String identifier, String advid, String placementid){
        this.androidid = androidid;
        this.identifier = identifier;
        this.advid = advid;
        this.placementid = placementid;
    }

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAdvid() {
        return advid;
    }

    public void setAdvid(String advid) {
        this.advid = advid;
    }

    public String getPlacementid() {
        return placementid;
    }

    public void setPlacementid(String placementid) {
        this.placementid = placementid;
    }
}
