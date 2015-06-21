package com.mobrand.mobrandsample;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;


/**
 * Created by rmateus on 22-03-2015.
 */
public class Mobrand extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();


        mContext = getApplicationContext();


    }


    public static Context getContext(){
        return mContext;
    }

}
