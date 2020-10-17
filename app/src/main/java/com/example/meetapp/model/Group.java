package com.example.meetapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class Group implements Serializable {

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

    public String addOrUpdateGroupGetID(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").push();
        this.setId(reference.getKey());
        reference.setValue(this);
        addUserToGroup();
        return reference.getKey();
    }

    public void addUserToGroup(){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.id).child("Members").child(CurrentUser.getInstance().getId()).setValue(CurrentUser.getInstance().getId());
        FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getInstance().getId()).child("Groups").child(this.id).setValue(this.id);
    }
}
