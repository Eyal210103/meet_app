package com.example.meetapp.model.meetings;

public class PublicMeeting extends Meeting {
    private String description;

    public PublicMeeting(long millis, String id, String subject ,String description) {
        super(millis, id,subject);
        this.description = description;
    }

    public PublicMeeting(String description) {
        super();
        this.description = description;
    }

    public PublicMeeting() {
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
                ", millis=" + millis +
                ", id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
