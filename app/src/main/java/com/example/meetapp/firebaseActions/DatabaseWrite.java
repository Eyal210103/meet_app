package com.example.meetapp.firebaseActions;

import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class DatabaseWrite {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String userRef = "Users";
    private static String groupsRef = "Groups";

    public static void addOrUpdateUser(User user){
        HashMap<String,Object> map = new HashMap<String, Object>();
        map.put("displayName",user.getDisplayName());
        map.put("email",user.getEmail());
        map.put("id",user.getId());
        map.put("profileImageUrl",user.getProfileImageUrl());
        database.getReference().child(userRef).child(user.getId()).updateChildren(map);
    }

    public static String addOrUpdateGroupGetID(Group group){
       DatabaseReference reference = database.getReference().child(groupsRef).push();
       group.setId(reference.getKey());
       reference.setValue(group);
       addUserToGroup(CurrentUser.getInstance().getId(),group.getId());
       return reference.getKey();
    }

    public static void addUserToGroup(String userId , String groupsId){
        database.getReference().child(groupsRef).child(groupsId).child("Members").child(userId).setValue(userId);
        database.getReference().child(userRef).child(userId).child("Groups").child(groupsId).setValue(groupsId);
    }
}
