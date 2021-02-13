package com.example.meetapp.model.meetings;

import com.example.meetapp.firebaseActions.FirebaseTags;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Meeting implements Serializable {
    protected long millis;
    protected String id;
    protected String subject;
    protected String description;
    protected double latitude;
    protected double longitude;


    public static String dateToString(Date date){
        return "" + date.getYear() + date.getMonth() + date.getDay();
    }

    public Meeting() {}

    public Meeting(long millis, String id, String subject, LatLng location) {
        this.millis = millis;
        this.id = id;
        this.subject = subject;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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


    public LatLng getLocation(){
        return new LatLng(latitude,longitude);
    }

//    public void setLocation(LatLng location){
//        this.latitude = location.latitude;
//        this.longitude = location.longitude;
//    }

    public String getDateString(){
        Date date = new Date(this.millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return "" + calendar.get(Calendar.YEAR) +  calendar.get(Calendar.MONTH) +  calendar.get(Calendar.DAY_OF_MONTH);
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void confirmUserArrival(String Uid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.PUBLIC_MEETINGS_CHILDES).child(this.id).child(FirebaseTags.WHO_COMING_CHILDES);
        reference.child(Uid).setValue(Uid);
    }

    public void updateOrAddReturnId(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.PUBLIC_MEETINGS_CHILDES).push();
        this.setId(reference.getKey());
        HashMap<String,Object> map = new HashMap<>();
        map.put("millis",millis);
        map.put("id",id);
        map.put("subject",subject);
        map.put("description",description);
        map.put("latitude",latitude);
        map.put("longitude",longitude);
        reference.updateChildren(map);

    }

    @Override
    public String toString() {
        return "Meeting{" +
                "millis=" + millis +
                ", id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
