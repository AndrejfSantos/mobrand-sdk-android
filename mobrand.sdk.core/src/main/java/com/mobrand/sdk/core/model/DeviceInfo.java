package com.mobrand.sdk.core.model;

/**
 * Created by rmateus on 01/12/15.
 */
public class DeviceInfo {

    private String appId;
    private String aId;
    private String advId;
    private String devId;
    private String macAddr;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "appId='" + appId + '\'' +
                ", aId='" + aId + '\'' +
                ", advId='" + advId + '\'' +
                ", devId='" + devId + '\'' +
                ", macAddr='" + macAddr + '\'' +
                '}';
    }
}
