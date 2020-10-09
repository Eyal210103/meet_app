package com.example.meetapp.model.meetings;

public class GroupMeeting extends Meeting {

    private boolean isOpen;

    public GroupMeeting(long millis, String id, String subject , boolean isOpen) {
        super(millis,id, subject);
        this.isOpen = isOpen;
    }

    public GroupMeeting(boolean isOpen) {
        super();
        this.isOpen = isOpen;
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
