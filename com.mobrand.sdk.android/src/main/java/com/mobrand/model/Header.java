package com.mobrand.model;

/**
 * Created by rmateus on 11/08/15.
 */
public class Header extends MobrandType{

    public Header(String name) {
        this.name = name;
    }

    public String name;

    public String getName() {
        return name;
    }

    @Override
    public ViewType getViewType() {
        return ViewType.HEADER;
    }
}
