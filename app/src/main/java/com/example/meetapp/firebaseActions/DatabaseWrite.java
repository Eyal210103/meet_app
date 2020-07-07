package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;

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

    public static void AddOrUpdateUser(User user){
        database.getReference().child(userRef).child(user.getId()).setValue(user);
    }



    public static String AddOrUpdateGroupGetID(Group group){
       DatabaseReference reference = database.getReference().child(groupsRef).push();
       group.setId(reference.getKey());
       reference.setValue(group);
       return reference.getKey().toString();
    }
}
