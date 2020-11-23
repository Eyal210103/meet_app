package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserMeetingsRepo {

    ArrayList<LiveData<GroupMeeting>> groupsMeetings = new ArrayList<>();
    ArrayList<LiveData<Meeting>> publicMeetings = new ArrayList<>();

    static UserMeetingsRepo instance =  null;

    public static UserMeetingsRepo getInstance() {
        if (instance == null)
            instance = new UserMeetingsRepo();
        return instance;
    }

    public MutableLiveData<ArrayList<LiveData<GroupMeeting>>> getGroupsMeetings(){
        MutableLiveData<ArrayList<LiveData<GroupMeeting>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(groupsMeetings);
        FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getInstance().getId()).child("Meetings").child("Group").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
       // mutableLiveData.
        return mutableLiveData;
    }
}
