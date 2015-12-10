package com.mobrand.sdk.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)

public class Ads {


    private List<Group> groups;
    private List<Ad> list;

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Ad> getList() {
        return list;
    }

    public void setList(List<Ad> list) {
        this.list = list;
    }
}
