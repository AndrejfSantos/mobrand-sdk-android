package com.mobrand.sdk.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)

public class AdsJson {


    private List<Ad> list;

    public List<Ad> getList() {
        return list;
    }

    public void setList(List<Ad> list) {
        this.list = list;
    }
}
