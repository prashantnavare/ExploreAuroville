package com.navare.prashant.exploreauroville.model;

/**
 * Created by prashant on 23-Apr-17.
 */

public class CurrentEvent {
    private int id;
    private String name;
    private int poi_id;
    private long from_date;
    private long to_date;
    private String description;
    private String tags;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPOI_id() {
        return poi_id;
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
}
