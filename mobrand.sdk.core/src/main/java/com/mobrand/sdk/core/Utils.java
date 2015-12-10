package com.mobrand.sdk.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.mobrand.sdk.core.model.Config;

/**
 * Created by rmateus on 01/12/15.
 */
public class Utils {

    public static String buildClickUrl(Config config, DeviceInfo info, String adid, String placementid){

        Uri uri = Uri.parse(config.getClicksEndpoint());

        Uri.Builder click = uri.buildUpon().appendPath("click");

        click.appendQueryParameter("appid", info.getAppId());
        click.appendQueryParameter("placementid", placementid);
        click.appendQueryParameter("androidid", info.getaId());
        click.appendQueryParameter("adid", adid);
        click.appendQueryParameter("advid", info.getAdvId());

        return click.build().toString();
    }

    public static String buildStoreUrl(String packageName){

        Uri uri = Uri.parse("market://details?id=" + packageName);

        return uri.toString();
    }

    public static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return String.valueOf(appInfo.metaData.getString(name));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // if we can’t find it in the manifest, just return null
        }

        return null;
    }

    public static String getIMEI(Context context){
        try{
            return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static  String getMacAddress(Context context) {
        String macAddress = null;
        try {
            WifiManager wimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            macAddress = wimanager.getConnectionInfo().getMacAddress();
        }catch (Exception e){
            e.printStackTrace();
        }
        return macAddress;
    }

}
