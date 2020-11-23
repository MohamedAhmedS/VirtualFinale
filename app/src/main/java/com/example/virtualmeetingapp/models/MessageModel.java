package com.example.virtualmeetingapp.models;

public class MessageModel {
    private long timeStamp;
    private boolean isSeen;
    private String message, receiver, sender, type;
//    private long ts;

    public MessageModel() {
    }

    public MessageModel(String message, String receiver, String sender, long timeStamp, String type, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timeStamp = timeStamp;
        this.type = type;
        this.isSeen = isSeen;
//        this.ts = ts;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timestamp) {
        this.timeStamp = timestamp;
    }
//    public long getTs() {
//        return ts;
//    }

//    public void setTs(String timestamp) {
//        this.timestamp = timestamp;
//    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
