package com.example.meetapp.model;

import com.example.meetapp.firebaseActions.FirebaseTags;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;

public class Group implements Serializable {

    private String name;
    private String id;
    private String subject;
    private String description;
    private String photoUrl;
    private boolean isPublic;

    public Group() {
    }

    public Group(String name, String id, String subject, String photoUrl, boolean isPublic) {
        this.name = name;
        this.id = id;
        this.subject = subject;
        this.photoUrl = photoUrl;
        this.isPublic = isPublic;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
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

    public String addGroup() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).push();
        this.id = reference.getKey();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("subject", this.subject);
        map.put("photoUrl", this.photoUrl);
        map.put("isPublic", this.isPublic);
        map.put("description", this.description);
        reference.updateChildren(map);
        addUserToGroup();
        reference.child("manager/"+CurrentUser.getInstance().getId()).setValue(CurrentUser.getInstance().getId());
        return reference.getKey();
    }

    public void updateGroup(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(id);
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", this.name);
        map.put("id", this.id);
        map.put("subject", this.subject);
        map.put("photoUrl", this.photoUrl);
        map.put("isPublic", this.isPublic);
        map.put("description", this.description);
        reference.updateChildren(map);
    }

    public void addUserToGroup(){
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.id).child(FirebaseTags.MEMBERS_CHILDES)
                .child(CurrentUser.getInstance().getId()).setValue(CurrentUser.getInstance().getId());
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES)
                .child(CurrentUser.getInstance().getId()).child(FirebaseTags.GROUPS_CHILDES).child(this.id).setValue(this.id);
    }
    public void requestToJoin(){
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.getId()).child(FirebaseTags.WAITING_CHILDES)
                .child(CurrentUser.getInstance().getId()).setValue(CurrentUser.getInstance().getId());
    }

}
