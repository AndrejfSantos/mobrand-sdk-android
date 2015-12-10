package com.mobrand.sdk.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by rmateus on 01/12/15.
 */
public enum CategoryEnum {

    APPS, GAMES;

    @JsonCreator
    public static CategoryEnum forValue(int num) {
        return CategoryEnum.values()[num];

    }

}
