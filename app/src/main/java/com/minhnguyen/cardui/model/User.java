package com.minhnguyen.cardui.model;

import android.content.Context;

import com.minhnguyen.cardui.R;

/**
 * Created by minhnguyen on 2/23/17.
 */

public class User {
    private Context mContext;

    private String userName;
    private String userGender;
    private String userPosition;
    private String userOrganization;
    private String userNumber;
    private String userBirthday;
    private String userIDNumber;
    private String userPhoneNumber;
    private String userEmail;

    public User() {

    }

    public User(String userName, String userGender, String userPosition, String userOrganization,
                String userNumber, String userBirthday, String userIDNumber, String userPhoneNumber, String userEmail) {
        this.userName = userName;
        this.userGender = userGender;
        this.userPosition = userPosition;
        this.userOrganization = userOrganization;
        this.userNumber = userNumber;
        this.userBirthday = userBirthday;
        this.userIDNumber = userIDNumber;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(String userPosition) {
        this.userPosition = userPosition;
    }

    public String getUserOrganization() {
        return userOrganization;
    }

    public void setUserOrganization(String userOrganization) {
        this.userOrganization = userOrganization;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserIDNumber() {
        return userIDNumber;
    }

    public void setUserIDNumber(String userIDNumber) {
        this.userIDNumber = userIDNumber;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // Item
    public void setContext(Context context) {
        mContext = context;
    }

    public Item getItemUserName() {
        return new Item(mContext.getString(R.string.header_user_name), getUserName());
    }

    public Item getItemUserGender() {
        return new Item(mContext.getString(R.string.header_user_gender), getUserGender());
    }

    public Item getItemUserPosition() {
        return new Item(mContext.getString(R.string.header_user_position), getUserPosition());
    }

    public Item getItemUserOrganization() {
        return new Item(mContext.getString(R.string.header_user_organization), getUserOrganization());
    }

    public Item getItemUserNumber() {
        return new Item(mContext.getString(R.string.header_user_number), getUserNumber());
    }

    public Item getItemUserBirthday() {
        return new Item(mContext.getString(R.string.header_user_birthday), getUserBirthday());
    }

    public Item getItemUserIDNumber() {
        return new Item(mContext.getString(R.string.header_user_ID_number), getUserIDNumber());
    }

    public Item getItemUserPhoneNumber() {
        return new Item(mContext.getString(R.string.header_user_phone_number), getUserPhoneNumber());
    }

    public Item getItemUserEmail() {
        return new Item(mContext.getString(R.string.header_user_email), getUserEmail());
    }
}
