package com.example.meetapp.model;

public class Message {
    private String senderId;
    private String senderDisplayName;
    private String context;
    private long time; // millis
    private String id;
    private String groupName;
    private String url;

    public Message(String senderId, String senderDisplayName, String context, long time) {
        this.senderId = senderId;
        this.senderDisplayName = senderDisplayName;
        this.context = context;
        this.time = time;
    }

    public Message() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId='" + senderId + '\'' +
                ", senderDisplayName='" + senderDisplayName + '\'' +
                ", context='" + context + '\'' +
                ", time=" + time +
                ", id='" + id + '\'' +
                ", groupName='" + groupName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
