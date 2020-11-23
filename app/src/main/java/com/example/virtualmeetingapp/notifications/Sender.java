package com.example.virtualmeetingapp.notifications;

public class Sender {

    private Data data;
    private String collapse_key;
    private String to;

    public Sender() {
    }

    public Sender(Data data, String to) {
        this.data = data;
        this.collapse_key = "type_a";
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCollapse_key() {
        return collapse_key;
    }

    public void setCollapse_key(String collapse_key) {
        this.collapse_key = collapse_key;
    }
}
