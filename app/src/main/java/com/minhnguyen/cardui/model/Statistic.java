package com.minhnguyen.cardui.model;

import java.util.Date;

/**
 * Created by minhnguyen on 2/23/17.
 */

public class Statistic {
    private Date date;
    private Organization organization;
    private User user;

    public Statistic() {

    }

    public Statistic(Date date, Organization organization, User user) {
        this.date = date;
        this.organization = organization;
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
