package com.example.meetapp.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Singleton;

@Singleton
public class CurrentUser {

    private static User user = null;

    private CurrentUser() {
    }

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user.setDisplayName(firebaseUser.getDisplayName());
        user.setEmail(firebaseUser.getEmail());
        user.setId(firebaseUser.getUid());
        user.setProfileImageUrl(firebaseUser.getPhotoUrl().toString());
        return user;
    }

    public static void setCurrentUser(User user) {
        CurrentUser.user = user;
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
        user = null;
    }

    public static boolean isConnected(){
        return user != null;
    }
}
