package com.example.virtualmeetingapp.models;

public class ConversationModel {
    private String id;
    private String lastMessage;
    private Long lastMessageTimeStamp;

    private String WithUID;
    private String userName;
    private String profileThumbnail;

    public ConversationModel() {
    }

    public ConversationModel(String id, String WithUID, String lastMessage, Long lastMessageTimeStamp) {
        this.id = id;
        this.WithUID = WithUID;
        this.lastMessage = lastMessage;
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWithUID() {
        return WithUID;
    }

    public void setWithUID(String withUID) {
        this.WithUID = withUID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Long getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(Long lastMessageTime) {
        this.lastMessageTimeStamp = lastMessageTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileThumbnail() {
        return profileThumbnail;
    }

    public void setProfileThumbnail(String profileThumbnail) {
        this.profileThumbnail = profileThumbnail;
    }
}

