package com.example.virtualmeetingapp.models;

public class Prisoner extends User {
    private String id;
    private String descriptionPrisoners;
    private String passwordPrisoners;

    public Prisoner() {
        id = "";
        descriptionPrisoners = "";
        passwordPrisoners = "";
    }

    public Prisoner(String id, String userType, String userName, String phoneNo, String userEmail,
                    String token, String descriptionPrisoners, String passwordPrisoners) {
        super(userType, userName, phoneNo, userEmail, token);
        this.id = id;
        this.descriptionPrisoners = descriptionPrisoners;
        this.passwordPrisoners = passwordPrisoners;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswordPrisoners() {
        return passwordPrisoners;
    }

    public void setPasswordPrisoners(String passwordPrisoners) {
        this.passwordPrisoners = passwordPrisoners;
    }

    public String getDescriptionPrisoners() {
        return descriptionPrisoners;
    }

    public void setDescriptionPrisoners(String descriptionPrisoners) {
        this.descriptionPrisoners = descriptionPrisoners;
    }
}