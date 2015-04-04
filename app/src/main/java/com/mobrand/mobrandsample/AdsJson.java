package com.mobrand.mobrandsample;

/**
 * Created by rmateus on 08-03-2015.
 */
public class AdsJson implements Ads {




    public String name;
    public Number rating;
    public String adid;
    public String icon_url;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIcon() {
        return icon_url;
    }

    @Override
    public String getAdid() {
        return adid;
    }

    @Override
    public Number getRating() {
        return rating;
    }
}
