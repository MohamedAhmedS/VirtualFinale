package com.example.virtualmeetingapp.models;

public class AppointmentDetail {
    private long appointmentId, callStartTimeInMillis, callEndTimeInMillis;
    private String prisonerName, visitorName,  type;

    public AppointmentDetail(){

    }

    public AppointmentDetail(long appointmentId, String prisonerName, String visitorName, long startTimeInMillis, long endTimeInMillis, String type) {
        this.appointmentId = appointmentId;
        this.prisonerName = prisonerName;
        this.visitorName = visitorName;
        this.callStartTimeInMillis = startTimeInMillis;
        this.callEndTimeInMillis = endTimeInMillis;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPrisonerName() {
        return prisonerName;
    }

    public void setPrisonerName(String prisonerName) {
        this.prisonerName = prisonerName;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public long getCallStartTimeInMillis() {
        return callStartTimeInMillis;
    }

    public void setCallStartTimeInMillis(long callStartTimeInMillis) {
        this.callStartTimeInMillis = callStartTimeInMillis;
    }

    public long getCallEndTimeInMillis() {
        return callEndTimeInMillis;
    }

    public void setCallEndTimeInMillis(long callEndTimeInMillis) {
        this.callEndTimeInMillis = callEndTimeInMillis;
    }
}
