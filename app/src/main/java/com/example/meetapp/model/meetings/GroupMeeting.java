package com.example.meetapp.model.meetings;

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
    public void updateOrAddReturnId() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Meetings").push();
        this.setId(reference.getKey());
        HashMap<String,Object> map = new HashMap<>();
        map.put("millis",millis);
        map.put("id",id);
        map.put("subject",subject);
        map.put("description",description);
        map.put("latitude",latitude);
        map.put("longitude",longitude);
        reference.updateChildren(map);
        if (isOpen){
            FirebaseDatabase.getInstance().getReference().child("Meetings").child("Group").child(this.id).setValue(this.groupId);
        }

    }
}
