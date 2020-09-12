package com.example.meetapp.notifications;

public class Sender {
    public  Data data;
    public String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    @Override
    public String toString() {
        return "Sender{" +
                "data=" + data +
                ", to='" + to + '\'' +
                '}';
    }
}
