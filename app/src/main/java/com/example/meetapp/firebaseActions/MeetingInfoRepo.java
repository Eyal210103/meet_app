package com.example.meetapp.firebaseActions;

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

    private final MutableLiveData<Meeting> meetingLiveData = new MutableLiveData<>();
    private final MutableLiveData<GroupMeeting> gMeetingLiveData = new MutableLiveData<>();
    private final String meetingId;

    public MeetingInfoRepo(String id) {
        this.meetingId = id;
    }

    public LiveData<Meeting> loadPublicMeeting() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.PUBLIC_MEETINGS_CHILDES)
                .child(this.meetingId).addValueEventListener(new ValueEventListener() {
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
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES)
                .child(groupId).child(FirebaseTags.MEETINGS_CHILDES).child(this.meetingId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GroupMeeting meeting = snapshot.getValue(GroupMeeting.class);
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
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.PUBLIC_MEETINGS_CHILDES).child(meetingId)
                .child(FirebaseTags.WHO_COMING_CHILDES)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
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
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(groupId)
                .child(FirebaseTags.MEETINGS_CHILDES).child(this.meetingId)
                .child(FirebaseTags.WHO_COMING_CHILDES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
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
