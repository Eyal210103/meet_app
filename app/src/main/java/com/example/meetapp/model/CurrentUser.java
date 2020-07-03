package com.example.meetapp.model;

public class CurrentUser {

    private static User user = null;

    private CurrentUser() {
    }

    public static User getCurrentUser() {
        return user;
    }

    public static void setCurrentUser(User user) {
        CurrentUser.user = user;
    }

    public static boolean isConnected(){
        return user != null;
    }
}
