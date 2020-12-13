package com.example.meetapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;

public class Group implements Serializable {

    private String name;
    private String id;
    private String subject;
    private String photoUrl;
    private boolean isPublic;

    public Group(String name, String id, String managerId, String subject, String photoUrl, boolean isPublic) {
        this.name = name;
        this.id = id;
        this.subject = subject;
        this.photoUrl = photoUrl;
        this.isPublic = isPublic;
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }

    public String addOrUpdateGroupGetID() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").push();
        this.setId(reference.getKey());
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        map.put("id", this.getId());
        map.put("subject", this.subject);
        map.put("photoUrl", this.photoUrl);
        map.put("isPublic", this.isPublic);
        reference.updateChildren(map);
        addUserToGroup();
        reference.child("manager/"+CurrentUser.getInstance().getId()).setValue(CurrentUser.getInstance().getId());
        return reference.getKey();
    }

    public void addUserToGroup(){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.id).child("Members").child(CurrentUser.getInstance().getId()).setValue(CurrentUser.getInstance().getId());
        FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getInstance().getId()).child("Groups").child(this.id).setValue(this.id);
    }

    public void requestToJoin(){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.getId()).child("Waiting")
                .child(CurrentUser.getInstance().getId()).setValue(CurrentUser.getInstance().getId());
    }

}
