package com.example.meetapp.model.message;

public class Message {
    private String senderId;
    private String senderDisplayName;
    private String context;
    private String hour; // HH:MM format
    private int day;

    public Message(String senderId, String senderDisplayName, String context, String hour, int day) {
        this.senderId = senderId;
        this.senderDisplayName = senderDisplayName;
        this.context = context;
        this.hour = hour;
        this.day = day;
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

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
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