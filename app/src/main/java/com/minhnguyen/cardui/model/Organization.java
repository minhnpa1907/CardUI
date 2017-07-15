package com.minhnguyen.cardui.model;

import com.minhnguyen.cardui.constant.StringValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minhnguyen on 2/24/17.
 */

public class Organization {
    private String organizationName;

    public Organization() {
    }

    public Organization(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public static List<String> createOrganizationsList() {
        List<String> organizations = new ArrayList<>();
        organizations.add(StringValue.LIST_ALL);

        return organizations;
    }
}
