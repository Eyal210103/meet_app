package com.example.meetapp.chatPushNotification;


public class Sender {
    public NotificationData data;
    public String to;

    public Sender(NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }
}