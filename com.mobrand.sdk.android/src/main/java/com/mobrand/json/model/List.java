package com.mobrand.json.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mobrand.view.model.MobrandType;
import com.mobrand.view.model.ViewType;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class List extends MobrandType implements IAds {


    public boolean alreadyImpressed;

    @JsonProperty("name")
    private String name;


    private String icon_url;

    @JsonProperty("adid")
    private String adid;

    private String store_url;

    @JsonProperty("rating")
    private Double rating;
    @JsonProperty("payout")
    private Double payout;
    @JsonProperty("packageName")
    private String packageName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("description")
    private String description;


    @Override
    public String toString() {
        return "List{" +
                "name='" + name + '\'' +
                ", icon_url='" + icon_url + '\'' +
                ", adid='" + adid + '\'' +
                ", rating=" + rating +
                ", payout=" + payout +
                ", packageName='" + packageName + '\'' +
                ", description='" + description + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

    @JsonProperty("store_url")
    @Override
    public String getStore_url() {
        return store_url;
    }

    @JsonProperty("store_url")
    public void setStore_url(String storeUrl) {
        this.store_url = storeUrl;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }


    /**
     *
     * @param name
     * The name
     */
    @JsonProperty("name")
    public void setName(String name) {

        this.name = name;
    }

    /**
     *
     * @return
     * The name
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }


    /**
     *
     * @param description
     * The name
     */
    @JsonProperty("name")
    public void setDescription(String description) {

        this.description = description;
    }




    /**
     *
     * @return
     * The icon_url
     */
    @JsonProperty("icon_url")
    public String getIcon_url() {
        return icon_url;
    }

    /**
     *
     * @param icon_url
     * The icon_url
     */
    @JsonProperty("icon_url")
    public void setIcon_url(String icon_url) {

        this.icon_url = icon_url;
    }

    /**
     *
     * @return
     * The adid
     */
    @JsonProperty("adid")
    public String getAdid() {
        return adid;
    }

    /**
     *
     * @param adid
     * The adid
     */
    @JsonProperty("adid")
    public void setAdid(String adid) {
        this.adid = adid;
    }

    /**
     *
     * @return
     * The rating
     */
    @JsonProperty("rating")
    public Double getRating() {
        return rating;
    }

    /**
     *
     * @param rating
     * The rating
     */
    @JsonProperty("rating")
    public void setRating(Double rating) {
        this.rating = rating;
    }

    /**
     *
     * @return
     * The payout
     */
    @JsonProperty("payout")
    public Double getPayout() {
        return payout;
    }

    /**
     *
     * @param payout
     * The payout
     */
    @JsonProperty("payout")
    public void setPayout(Double payout) {
        this.payout = payout;
    }

    /**
     *
     * @return
     * The packageName
     */
    @JsonProperty("packageName")
    public String getPackageName() {
        return packageName;
    }

    /**
     *
     * @param packageName
     * The packageName
     */
    @JsonProperty("packageName")
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {



        this.additionalProperties.put(name, value);
    }

    @Override
    public ViewType getViewType() {
        return ViewType.ITEM;
    }
}