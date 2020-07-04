package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;

import com.example.meetapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseWrite {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String userRef = "Users";
    private static String groupsRef = "Groups";

    public static void AddOrUpdateUser(User user){
        database.getReference().child(userRef).child(user.getId()).setValue(user);
    }

    public static void AddOrUpdateGroup(Object group){
       // database.getReference().child(userRef).child(user.getId()).setValue(user);
    }
}
