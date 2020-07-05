package com.example.meetapp.model.meetings;

public class PublicMeeting extends Meeting {
    private String description;

    public PublicMeeting(int year, int month, int day, int hour, int minutes, String id, String description) {
        super(year, month, day, hour, minutes, id);
        this.description = description;
    }

    public PublicMeeting(String description) {
        super();
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PublicMeeting{" +
                "description='" + description + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minutes=" + minutes +
                ", id='" + id + '\'' +
                '}';
    }
}
