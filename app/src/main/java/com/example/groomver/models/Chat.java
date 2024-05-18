package com.example.groomver.models;

public class Chat {
    private String userName;
    private String userAvatar;
    private long timestamp;
    private String Key;
    private String text;


    public Chat(String userName, String userAvatar, long timestamp, String text, String Key) {
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.timestamp = timestamp;
        this.text = text;
        this.Key = Key;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timeStamp) {
        this.timestamp = timeStamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }
}