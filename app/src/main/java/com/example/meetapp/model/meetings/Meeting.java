package com.example.meetapp.model.meetings;

public abstract class Meeting {
    long millis;
    String id;
    String subject;

    public Meeting() {
    }

    public Meeting(long millis, String id, String subject) {
        this.millis = millis;
        this.id = id;
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "millis=" + millis +
                ", id='" + id + '\'' +
                '}';
    }
}
