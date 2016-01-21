package com.mobrand.model;

import com.mobrand.sdk.core.model.TimeoutStats;

import java.util.Locale;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;

/**
 * Created by rmateus on 12/01/16.
 */
public interface  Webservices {

    @GET("/androidGroups")
    @Headers("Accept: " + Constants.MOBRAND_API_V1)
    void getAndroidGroups(@Header("Accept-Language") String language, Callback<GroupStringsMapJson> callback );
}
