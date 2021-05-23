package com.example.meetapp.model;

public class Message {
    /**
     * represents the sender id
     */
    private String senderId;
    /**
     * represents the sender's display name
     */
    private String senderDisplayName;
    /**
     * represents the text that is being sent
     */
    private String context;
    /**
     * represents the time when the message has sent
     */
    private long time;
    /**
     * represents the unique id of the message
     */
    private String id;
    /**
     * represents the name of the group where the message is from
     */
    private String groupName;
    /**
     * represents the url to a pic of the message - @nullable
     */
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
