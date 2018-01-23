package com.navare.prashant.shared.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by prashant on 10/11/2017.
 */

public class Guest {
    private int id;
    private String name;
    private String phone;
    private long from_date;
    private long to_date;
    private String sponsor;
    private String location;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public long getFrom_date() {
        return from_date;
    }

    public long getTo_date() {
        return to_date;
    }

    public String getValidFrom() {
        Calendar validityDate = Calendar.getInstance();
        validityDate.setTimeInMillis(from_date);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        return dateFormatter.format(validityDate.getTime());
    }

    public String getValidTill() {
        Calendar validityDate = Calendar.getInstance();
        validityDate.setTimeInMillis(to_date);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
        return dateFormatter.format(validityDate.getTime());
    }

    public String getSponsor() {
        return sponsor;
    }

    public String getLocation() {
        return location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFrom_date(long from_date) {
        this.from_date = from_date;
    }

    public void setTo_date(long to_date) {
        this.to_date = to_date;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
