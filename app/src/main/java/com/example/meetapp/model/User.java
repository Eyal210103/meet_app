package com.example.meetapp.model;

public class User {
    /**
     * represents the unique id of the user
     */
    private String id;
    /**
     * represents the email of the user
     */
    private String email;
    /**
     * represents a url to the profile pic of the user
     */
    private String profileImageUrl;
    /**
     * represents the display name of the user
     */
    private String displayName;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        if (email == null){
           return "null";
        }
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }


}
