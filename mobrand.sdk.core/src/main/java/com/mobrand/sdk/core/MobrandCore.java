package com.mobrand.sdk.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.mobrand.sdk.core.model.Ad;
import com.mobrand.sdk.core.model.Ads;
import com.mobrand.sdk.core.model.Config;
import com.mobrand.sdk.core.model.Group;
import com.mobrand.sdk.core.model.NativeAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;

/**
 * Created by rmateus on 30/11/15.
 */
public class MobrandCore {

    private MobrandWebView mobrandWebView;
    private DeviceInfo devInfo;
    private Config config;
    private final static String METADATA_KEY = "mobrand_appid";

    private static final String URL = "http://api.mobrand.net";
    RestAdapter adapter = new RestAdapter.Builder().setEndpoint(URL).build();

    MobrandCallback callback = new MobrandCallback() {
        @Override
        public void onReady(MobrandCore core) {

        }

        @Override
        public void onError() {

        }
    };
    private boolean destroyed;

    public void setCallback(MobrandCallback callback) {
        this.callback = callback;
    }

    private ImpressionsEngine impressionsEngine;

    CountDownLatch latch = new CountDownLatch(1);
    int retry = 1;

    public void create(final Context context) {
        initDeviceInfo(context);

        System.out.println("creating");

        adapter.create(Webservices.class).getConfig(new Callback<Config>() {

            @Override
            public void success(Config config, Response response) {

                MobrandCore.this.config = config;

                MobrandCore.this.mobrandWebView = new MobrandWebView(context, config, devInfo);

                final Handler handler = new Handler();

                impressionsEngine = getImpressionsEngine();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            latch.await();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (!destroyed) {
                                        callback.onReady(MobrandCore.this);
                                    }
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }

            @Override
            public void failure(RetrofitError retrofitError) {


                if (retry <= 3) {

                    String URL = "http://static" + retry + ".mobrand.net";
                    RestAdapter adapter = new RestAdapter.Builder().setEndpoint(URL).build();
                    adapter.create(Webservices.class).getConfig(this);
                    retry++;

                } else {
                    if (!destroyed) {
                        callback.onError();
                    }

                }
            }
        });


    }

    private void initDeviceInfo(final Context context) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    devInfo = new DeviceInfo();
                    String appid = Utils.getMetadata(context, METADATA_KEY);
                    String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                    devInfo.setAppId(appid);
                    devInfo.setaId(androidId);
                    devInfo.setDevId(Utils.getIMEI(context));
                    devInfo.setMacAddr(Utils.getMacAddress(context));

                    String adId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
                    devInfo.setAdvId(adId);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            }
        }).start();
    }


    public DeviceInfo getDevInfo(){
        return devInfo;
    }

    public Config getConfig(){
        return config;
    }


    public MobrandWebView getMobrandWebView(){
        return mobrandWebView;
    }

    public void destroy(){
        mobrandWebView.destroy();
        impressionsEngine.stop();
        destroyed = true;
    }


    private ImpressionsEngine getImpressionsEngine(){

        if(config == null ){
            throw new IllegalStateException("Config hasn't been received yet");
        }

        return ImpressionsEngine.getInstance(devInfo, config);
    }

    public List<Ad> getAds(String placementId){

        if(config == null ){
            throw new IllegalStateException("Config hasn't been received yet");
        }

        RestAdapter adapter = new RestAdapter.Builder().setEndpoint(config.getAdsEndpoint()).build();

        Ads ads = adapter.create(Webservices.class).getAds(devInfo.getAppId(), placementId, null);

        return ads.getList();

    }



    public  interface GetAdsCallback{
        void onSuccess(List<Ad> ads);
        void onFailure();

    }

    public void getAdsAsync(String placementId, final GetAdsCallback callback) {

        adapter.create(Webservices.class).getAdsAsync(devInfo.getAppId(), placementId, null, new Callback<Ads>() {
            @Override
            public void success(Ads ads, Response response) {
                if(!destroyed) {
                    callback.onSuccess(ads.getList());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if(!destroyed){
                    callback.onFailure();
                }
            }
        });


    }

        public void addImpression(String adid, String placementid){
        impressionsEngine.addImpression(adid, placementid);
    }

    public interface ClickCallback{
        void onReady();
    }

    public void click(String adid, String placementid, int position, ClickCallback callback){

        System.out.println("onClick");
        getMobrandWebView().getClient().load(adid, placementid, position, callback);
    }

    public static void startMarketFromPackage(Context context, String packageName){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.android.vending");
        //Toast.makeText(v.getContext(), , Toast.LENGTH_LONG).show();
        intent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(intent);
    }


}
