package com.example.meetapp.model.message;

import com.example.meetapp.model.ConstantValues;

public class Message {
    private String senderId;
    private String senderDisplayName;
    private String context;
    private long hour; // millis
    private String id;
    private String groupName;
    private int day;
    protected int type;

    public Message(String senderId, String senderDisplayName, String context, long hour, int day) {
        this.senderId = senderId;
        this.senderDisplayName = senderDisplayName;
        this.context = context;
        this.hour = hour;
        this.day = day;
        type = ConstantValues.TYPE_TEXT;
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

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId='" + senderId + '\'' +
                ", senderDisplayName='" + senderDisplayName + '\'' +
                ", hour='" + hour + '\'' +
                ", day=" + day +
                '}';
    }
}
