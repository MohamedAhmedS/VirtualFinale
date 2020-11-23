package com.example.virtualmeetingapp.models;

public class User  {
    private String uid;
    private String token;
    private String userType;
    private String userName;
    private String phoneNo;
    private String userEmail;
    private String profileThumb;
    private boolean onlineStatus;

    public User(String userType, String userName, String phoneNo, String userEmail, String token) {
        this.userType = userType;
        this.userName = userName;
        this.phoneNo = phoneNo;
        this.userEmail = userEmail;
        this.token = token;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProfileThumb() {
        return profileThumb;
    }

    public void setProfileThumb(String profileThumb) {
        this.profileThumb = profileThumb;
    }

    public boolean isOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}

