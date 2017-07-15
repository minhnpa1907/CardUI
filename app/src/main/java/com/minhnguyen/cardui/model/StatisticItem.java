package com.minhnguyen.cardui.model;

import java.util.Date;

/**
 * Created by minhnguyen on 2/24/17.
 */

public class StatisticItem {
    private String name;
    private String organization;
    private Date date;

    public StatisticItem() {
    }

    public StatisticItem(String name, String organization, Date date) {
        this.name = name;
        this.organization = organization;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
