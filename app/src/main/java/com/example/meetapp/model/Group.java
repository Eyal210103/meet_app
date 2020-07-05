package com.example.meetapp.model;

public class Group {

    private String name;
    private String id;
    private String managerId;
    private String subject;
    private String photoUrl;

    public Group(String name, String id, String managerId, String subject, String photoUrl) {
        this.name = name;
        this.id = id;
        this.managerId = managerId;
        this.subject = subject;
        this.photoUrl = photoUrl;
    }

    public Group() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", managerId='" + managerId + '\'' +
                ", subject='" + subject + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
