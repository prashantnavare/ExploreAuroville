package com.navare.prashant.shared.model;

/**
 * Created by prashant on 17-Apr-17.
 */

public class Location {
    private int     id;
    private String  name;
    private String city_id;
    private String  latitude;
    private String  longitude;
    private String  address;
    private String  zip;
    private String website;
    private String  description;
    private String  tags;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getZip() {
        return zip;
    }

    public String getWebsite() {
        return website;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return name;
    }
}
