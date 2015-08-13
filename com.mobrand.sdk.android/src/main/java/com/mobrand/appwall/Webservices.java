package com.mobrand.appwall;

import com.mobrand.json.model.Ads;
import com.mobrand.json.model.Config;
import com.mobrand.json.model.Impression;

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

    @GET("/android-config")
    Config getConfig();

    @GET("/ads")
    @Headers("Accept: application/vnd.mobrand.v1+json, application/json")
    void getAds( @Query("appid") String appid, @Query("placement") String placement, @Query("category") String category, @Query("country") String country, Callback<List<Ads>> callback);

    @POST("/impression")
    Response postImpression(@Body List<Impression> impression);




}
