package com.mobrand.sdk.core.model;

import java.util.List;

/**
 * Created by rmateus on 30/11/15.
 */
public class TimeoutStats {

    private String adid;
    private long time;
    private int position;
    private List<String> urls;

    public TimeoutStats(String adid, long time, int position, List<String> urls) {
        this.adid = adid;
        this.time = time;
        this.position = position;
        this.urls = urls;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
