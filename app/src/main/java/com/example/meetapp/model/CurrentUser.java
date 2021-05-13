package com.example.meetapp.model;

import com.example.meetapp.firebaseActions.FirebaseTags;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CurrentUser {

    private static User user = null;

    private CurrentUser() {
    }

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        user.setDisplayName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        user.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        user.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.setProfileImageUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
        return user;
    }

    public static void addOrUpdateUser(){
        getInstance(); //null safe
        HashMap<String,Object> map = new HashMap<String, Object>();
        map.put("displayName",user.getDisplayName());
        map.put("email",user.getEmail());
        map.put("id",user.getId());
        map.put("profileImageUrl",user.getProfileImageUrl());
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(user.getId()).updateChildren(map);
        getInstance(); //update
    }

    public static void joinMeeting(String id,String type , String group) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(user.getId()).child(type).child(id).setValue(group);
    }
    public static void quitMeeting(String id,String type) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(user.getId()).child(type).child(id).removeValue();
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
        user = null;
    }

    public static boolean isConnected(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


    public static void setCurrentUser(User user) {
        CurrentUser.user = user;
    }

}
