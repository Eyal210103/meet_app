package com.example.meetapp.model.meetings;

public class GroupMeeting extends Meeting {

    private boolean isOpen;

    public GroupMeeting(int year, int month, int day, int hour, int minutes, String id, boolean isOpen) {
        super(year, month, day, hour, minutes, id);
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
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", id='" + id + '\'' +
                '}';
    }
}
