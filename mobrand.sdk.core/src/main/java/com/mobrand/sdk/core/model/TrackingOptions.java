package com.mobrand.sdk.core.model;

/**
 * Created by rmateus on 26/11/15.
 */
public class TrackingOptions {

    private String advId;
    private String macAddr;
    private String aId;
    private String deviceId;

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getaId() {
        return aId;
    }

    public void setaId(String aId) {
        this.aId = aId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "TrackingOptions{" +
                "advId='" + advId + '\'' +
                ", macAddr='" + macAddr + '\'' +
                ", aId='" + aId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
