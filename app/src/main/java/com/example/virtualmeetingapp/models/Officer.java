package com.example.virtualmeetingapp.models;

public class Officer extends User {
    private String id;
    private String descriptionOfficer;
    private String passwordOfficer;
    private boolean officerListAcceptByAdmin;
    private boolean visitorListAcceptByAdmin;

    public Officer() {
        id = "";
        descriptionOfficer = "";
        passwordOfficer = "";
        officerListAcceptByAdmin = true;
        visitorListAcceptByAdmin = true;
    }

    public Officer(String id, String userType, String userName, String phoneNo, String userEmail,
                   String token, String descriptionOfficer, String passwordOfficer,
                   Boolean officerListAcceptByAdmin, Boolean visitorListAcceptByAdmin) {
        super(userType, userName, phoneNo, userEmail, token);
        this.id = id;
        this.descriptionOfficer = descriptionOfficer;
        this.passwordOfficer = passwordOfficer;
        this.officerListAcceptByAdmin = officerListAcceptByAdmin;
        this.visitorListAcceptByAdmin = visitorListAcceptByAdmin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswordOfficer() {
        return passwordOfficer;
    }

    public void setPasswordOfficer(String passwordOfficer) {
        this.passwordOfficer = passwordOfficer;
    }

    public boolean isOfficerListAcceptByAdmin() {
        return officerListAcceptByAdmin;
    }

    public void setOfficerListAcceptByAdmin(boolean officerListAcceptByAdmin) {
        this.officerListAcceptByAdmin = officerListAcceptByAdmin;
    }

    public boolean isVisitorListAcceptByAdmin() {
        return visitorListAcceptByAdmin;
    }

    public void setVisitorListAcceptByAdmin(boolean visitorListAcceptByAdmin) {
        this.visitorListAcceptByAdmin = visitorListAcceptByAdmin;
    }

    public String getDescriptionOfficer() {
        return descriptionOfficer;
    }

    public void setDescriptionOfficer(String descriptionOfficer) {
        this.descriptionOfficer = descriptionOfficer;
    }
}
