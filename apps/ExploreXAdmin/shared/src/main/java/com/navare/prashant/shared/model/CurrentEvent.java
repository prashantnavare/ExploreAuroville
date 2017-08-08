package com.navare.prashant.shared.model;

/**
 * Created by prashant on 23-Apr-17.
 */

public class CurrentEvent {
    private int id;
    private String name;
    private String from_date;
    private String to_date;
    private String description;
    private String tags;
    private String location;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public long getFrom_date() {
        return Long.valueOf(from_date);
    }

    public long getTo_date() {
        return Long.valueOf(to_date);
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

    public void setLocation(String locationString) {
        this.location = locationString;
    }

    public void setFrom_date(long from_date) {
        this.from_date = String.valueOf(from_date);
    }

    public void setTo_date(long to_date) {
        this.to_date = String.valueOf(to_date);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
