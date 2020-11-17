package com.example.meetapp.model.meetings;

import com.google.android.gms.maps.model.LatLng;

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
}
