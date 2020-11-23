package com.example.virtualmeetingapp.models;

public class AppointTimeItem {
    private String tvTime;
    private Boolean isAvailable;

    public AppointTimeItem(String tvTime) {
        this.tvTime = tvTime;
        this.isAvailable = true;
    }

    public String getTvTime() {
        return tvTime;
    }

    public Boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}
