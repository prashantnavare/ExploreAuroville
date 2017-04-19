package com.navare.prashant.exploreauroville.model;

/**
 * Created by prashant on 17-Apr-17.
 */

public class POI {
    private int     id;
    private String  name;
    private String  location_id;
    private String  latitude;
    private String  longitude;
    private String  address;
    private String  zip;
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

    public String getLocation_id() {
        return location_id;
    }

    public String getTags() {
        return tags;
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

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return name;
    }
}
