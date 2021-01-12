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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserMeetingsRepo {

    private final HashMap<String, LiveData<Meeting>> allMeetings;
    private final MutableLiveData<HashMap<String, LiveData<Meeting>>> mutableLiveData;

    private static UserMeetingsRepo instance = null;

    private UserMeetingsRepo() {
        allMeetings = new HashMap<>();
        mutableLiveData = new MutableLiveData<>();
    }

    public static UserMeetingsRepo getInstance() {
        if (instance == null) {
            instance = new UserMeetingsRepo();
            instance.loadPublicMeetings();
            instance.loadGroupsMeetings();
        }
        return instance;
    }

    public LiveData<HashMap<String, LiveData<Meeting>>> getAllMeetings() {
        mutableLiveData.setValue(allMeetings);
        return mutableLiveData;
    }


    private void loadGroupsMeetings() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance().getId())
                .child(FirebaseTags.GROUP_MEETINGS_CHILDES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String group = snapshot.getValue(String.class);
                FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(group).child(FirebaseTags.MEETINGS_CHILDES).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GroupMeeting meeting = snapshot.getValue(GroupMeeting.class);
                        MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                        meetingMutableLiveData.setValue(meeting);
                        allMeetings.put(meeting.getDateString(), meetingMutableLiveData);
                        mutableLiveData.postValue(allMeetings);
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
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadPublicMeetings() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance().getId()).child(FirebaseTags.PUBLIC_MEETINGS_CHILDES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child(FirebaseTags.PUBLIC_MEETINGS_CHILDES).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Meeting meeting = snapshot.getValue(Meeting.class);
                            MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                            meetingMutableLiveData.setValue(meeting);
                            allMeetings.put(meeting.getDateString(), meetingMutableLiveData);
                            mutableLiveData.postValue(allMeetings);
                        }
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
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
