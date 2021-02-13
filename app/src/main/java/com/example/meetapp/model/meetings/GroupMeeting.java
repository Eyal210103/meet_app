package com.example.meetapp.model.meetings;

import com.example.meetapp.firebaseActions.FirebaseTags;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class GroupMeeting extends Meeting {

    private boolean isOpen;
    private String groupId;

    public GroupMeeting(long millis, String id, String subject, LatLng location, boolean isOpen, String groupId) {
        super(millis,id,subject,location);
        this.isOpen = isOpen;
        this.groupId = groupId;
    }

    @Override
    public void setLatitude(double latitude) {
        super.setLatitude(latitude);
    }

    @Override
    public void setLongitude(double longitude) {
        super.setLongitude(longitude);
    }

    @Override
    public void setSubject(String subject) {
        super.setSubject(subject);
    }

    @Override
    public void setMillis(long millis) {
        super.setMillis(millis);
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(description);
    }

    public GroupMeeting() {
        super();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public String toString() {
        return "GroupMeeting{" +
                "isOpen=" + isOpen +
                ", millis=" + millis +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public void confirmUserArrival(String Uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(groupId).child(FirebaseTags.MEETINGS_CHILDES).child(this.id).child("whoComing");
        reference.child(Uid).setValue(Uid);
    }

    @Override
    public void updateOrAddReturnId() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(groupId).child(FirebaseTags.MEETINGS_CHILDES).push();
        this.setId(reference.getKey());
        HashMap<String,Object> map = new HashMap<>();
        map.put("millis",millis);
        map.put("id",id);
        map.put("subject",subject);
        map.put("description",description);
        map.put("latitude",latitude);
        map.put("longitude",longitude);
        map.put("isOpen",isOpen);
        map.put("groupId",groupId);
        reference.updateChildren(map);
        if (isOpen){
            FirebaseDatabase.getInstance().getReference().child(FirebaseTags.MEETINGS_CHILDES).child(FirebaseTags.GROUP_TYPE_CHILDES).child(this.id).setValue(this.groupId);
        }
    }
}
