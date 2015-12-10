package com.mobrand.sdk.core;

import com.mobrand.sdk.core.model.Ads;
import com.mobrand.sdk.core.model.Config;
import com.mobrand.sdk.core.model.Impression;
import com.mobrand.sdk.core.model.TimeoutStats;
import com.mobrand.sdk.core.model.TrackingOptions;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by rmateus on 11/08/15.
 */
public interface Webservices {

    @GET("/androidConfig")
    @Headers("Accept: application/vnd.api.mobrand.net-v1+json, application/json")
    void getConfig(Callback<Config> callback);

    @GET("/ads/v2?platform=android")
    @Headers("Accept: application/vnd.api.mobrand.net-v1+json")
    void getAdsAsync(@Query("appid") String appid, @Query("placementid") String placement, @Query("country") String country, Callback<Ads> callback);

    @GET("/ads/v2?platform=android")
    @Headers("Accept: application/vnd.api.mobrand.net-v1+json")
    Ads getAds(@Query("appid") String appid, @Query("placementid") String placement, @Query("country") String country);


    @POST("/impression")
    @Headers({"Content-Type: application/json", "Accept: application/v2+json"})
    Response postImpression(@Body Impression impression);


    @POST("/timeoutStats")
    Response postTimeoutStats(@Body TimeoutStats impression);

    @GET("/trackingConfig")
    @Headers("Accept: application/vnd.api.mobrand.net-v1+json")
    TrackingOptions getTrackingConfig();

}
