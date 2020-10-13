package com.example.meetapp.model.meetings;

import com.google.android.gms.maps.model.LatLng;

public abstract class Meeting {
    long millis;
    String id;
    String subject;
    double latitude;
    double longitude;

    public Meeting() {}

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public void setLocation(LatLng location){
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "millis=" + millis +
                ", id='" + id + '\'' +
                '}';
    }
}
