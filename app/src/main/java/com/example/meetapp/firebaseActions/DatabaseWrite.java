package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;

import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class DatabaseWrite {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String userRef = "Users";
    private static String groupsRef = "Groups";

    public static void addOrUpdateUser(User user){
        database.getReference().child(userRef).child(user.getId()).setValue(user);
    }

    public static String addOrUpdateGroupGetID(Group group){
       DatabaseReference reference = database.getReference().child(groupsRef).push();
       group.setId(reference.getKey());
       reference.setValue(group);
       addUserToGroup(CurrentUser.getCurrentUser().getId(),group.getId());
       return reference.getKey();
    }

    public static void addUserToGroup(String userId , String groupsId){
        database.getReference().child(groupsRef).child(groupsId).child("Members").child(userId).setValue(userId);
        database.getReference().child(userRef).child(userId).child("Groups").child(groupsId).setValue(groupsId);
    }
}
