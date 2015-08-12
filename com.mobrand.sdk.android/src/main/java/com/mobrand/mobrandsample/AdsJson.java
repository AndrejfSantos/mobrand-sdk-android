package com.mobrand.mobrandsample;

import com.mobrand.json.model.IAds;
import com.mobrand.model.MobrandType;
import com.mobrand.model.ViewType;

/**
 * Created by rmateus on 08-03-2015.
 */
public class AdsJson extends MobrandType implements IAds {




    public String name;
    public Number rating;
    public String adid;
    public String icon_url;
    public String description;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIcon_url() {
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

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getStore_url() {
        return null;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.ITEM;
    }
}
