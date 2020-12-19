package com.example.meetapp.firebaseActions;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.meetings.Meeting;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AvailableMeetingsRepo {

    private ArrayList<MutableLiveData<Meeting>> publicMeetings = new ArrayList<>();
    public HashMap<String,Integer> publicHash = new HashMap<>();


    private static AvailableMeetingsRepo instance =  null;

    private AvailableMeetingsRepo() {
    }

    public static AvailableMeetingsRepo getInstance() {
        if (instance == null)
            instance = new AvailableMeetingsRepo();
        return instance;
    }

    public MutableLiveData<ArrayList<MutableLiveData<Meeting>>> getPublicMeetings(){
        MutableLiveData<ArrayList<MutableLiveData<Meeting>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(publicMeetings);
        FirebaseDatabase.getInstance().getReference().child("Meetings").child("Public").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Meeting meeting = snapshot.getValue(Meeting.class);
                MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                meetingMutableLiveData.setValue(meeting);
                publicHash.put(meeting.getId(),publicMeetings.size());
                publicMeetings.add(meetingMutableLiveData);
                Log.d("AvailableMeetingsRepo", "++++++++++++++++++++++++++++++++++++++++++++++++++onChildAdded: " + meeting.toString());
                mutableLiveData.postValue(publicMeetings);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int i = publicHash.get(snapshot.getKey());
                publicMeetings.get(i).postValue(snapshot.getValue(Meeting.class));
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //TODO implement
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return mutableLiveData;
    }
}
