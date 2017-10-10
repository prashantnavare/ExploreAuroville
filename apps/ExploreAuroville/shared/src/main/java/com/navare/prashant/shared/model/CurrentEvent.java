package com.navare.prashant.shared.model;

/**
 * Created by prashant on 23-Apr-17.
 */

public class CurrentEvent {
    private int id;
    private String name;
    private long from_date;
    private long to_date;
    private String description;
    private String tags;
    private String location;
    private int accessLevel;

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
        return from_date;
    }

    public long getTo_date() {
        return to_date;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocationx(String location) {
        this.location = location;
    }

    public void setFrom_date(long from_date) {
        this.from_date = from_date;
    }

    public void setTo_date(long to_date) {
        this.to_date = to_date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
