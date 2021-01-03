package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.Meeting;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AvailableMeetingsRepo {

    private ArrayList<MutableLiveData<Meeting>> publicMeetings = new ArrayList<>();
    private HashMap<String,Integer> publicHash = new HashMap<>();
    private long todayMillis;

    private static AvailableMeetingsRepo instance =  null;

    private AvailableMeetingsRepo() {
    }

    public static AvailableMeetingsRepo getInstance() {
        if (instance == null) {
            instance = new AvailableMeetingsRepo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<MutableLiveData<Meeting>>> getPublicMeetings(){
        todayMillis = Calendar.getInstance().getTimeInMillis();
        MutableLiveData<ArrayList<MutableLiveData<Meeting>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(publicMeetings);
        FirebaseDatabase.getInstance().getReference().child("Meetings").child("Public").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Meeting meeting = snapshot.getValue(Meeting.class);
                if(meeting.getMillis() > todayMillis){
                    MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                    meetingMutableLiveData.setValue(meeting);
                    publicHash.put(meeting.getId(), publicMeetings.size());
                    publicMeetings.add(meetingMutableLiveData);
                    mutableLiveData.postValue(publicMeetings);
                }
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

    public static LiveData<ArrayList<User>> loadWhoComing(String mId){
        ArrayList<User> arrayList = new ArrayList<User>();
        MutableLiveData<ArrayList<User>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(arrayList);
        FirebaseDatabase.getInstance().getReference().child("Meetings").child("Public").child(mId).child("whoComing").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.add(snapshot.getValue(User.class));
                        mutableLiveData.postValue(arrayList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //TODO
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
        return mutableLiveData;
    }

    public HashMap<String, Integer> getPublicHash() {
        return publicHash;
    }
}
