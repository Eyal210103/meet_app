package com.example.meetapp.firebaseActions;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MeetingInfoRepo {

    private MutableLiveData<Meeting> meetingLiveData = new MutableLiveData<>();
    private MutableLiveData<GroupMeeting> gMeetingLiveData = new MutableLiveData<>();
    private String id;

    public MeetingInfoRepo(String id) {
        this.id = id;
    }

    public LiveData<Meeting> loadPublicMeeting() {
        FirebaseDatabase.getInstance().getReference().child("Meetings").child("Public").child(this.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Meeting meeting = snapshot.getValue(Meeting.class);
                meetingLiveData.postValue(meeting);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return meetingLiveData;
    }

    public LiveData<GroupMeeting> loadGroupMeeting(String groupId) {
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Meetings").child(this.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GroupMeeting meeting = snapshot.getValue(GroupMeeting.class);
                //        Log.d("MeetingInfoRepo", "onDataChange: " + meeting);
                gMeetingLiveData.postValue(meeting);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return gMeetingLiveData;
    }

    public LiveData<ArrayList<User>> loadWhoComing() {
        ArrayList<User> arrayList = new ArrayList<User>();
        MutableLiveData<ArrayList<User>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(arrayList);
        FirebaseDatabase.getInstance().getReference().child("Meetings").child("Public").child(id).child("whoComing").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.add(snapshot.getValue(User.class));
                        Log.d("TAG", "onDataChange: " + snapshot.getValue(User.class));
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
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
        return mutableLiveData;
    }

    public LiveData<ArrayList<User>> loadWhoComing(String groupId) {
        ArrayList<User> arrayList = new ArrayList<User>();
        MutableLiveData<ArrayList<User>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(arrayList);
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Meetings").child(this.id).child("whoComing").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.add(snapshot.getValue(User.class));
                        Log.d("TAG", "onDataChange: " + snapshot.getValue(User.class));
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
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
        return mutableLiveData;
    }

}
