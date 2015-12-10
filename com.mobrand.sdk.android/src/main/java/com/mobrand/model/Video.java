package com.mobrand.view.model;

/**
 * Created by rmateus on 11/08/15.
 */
public class Video extends MobrandType {

    private final String name;

    public Video(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public ViewType getViewType() {
        return ViewType.VIDEO;
    }
}