package com.minhnguyen.cardui.model;

/**
 * Created by minhnguyen on 1/12/17.
 */

public class Item {
    private String mKey;
    private String mValue;

    public Item() {

    }

    public Item(String mKey, String mValue) {
        this.mKey = mKey;
        this.mValue = mValue;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String mValue) {
        this.mValue = mValue;
    }
}
