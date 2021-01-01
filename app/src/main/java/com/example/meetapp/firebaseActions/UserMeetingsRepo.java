package com.example.meetapp.firebaseActions;

import android.util.Log;

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

import java.util.ArrayList;
import java.util.HashMap;

public class UserMeetingsRepo {
    private static final String TAG = "USERMREPO";
    private ArrayList<LiveData<GroupMeeting>> groupsMeetings = new ArrayList<>();
    private ArrayList<LiveData<Meeting>> publicMeetings = new ArrayList<>();
    private HashMap<String,Integer> publicHash = new HashMap<>();
    private HashMap<String,Integer> groupHash = new HashMap<>();

    private static UserMeetingsRepo instance =  null;

    private UserMeetingsRepo() {
    }

    public static UserMeetingsRepo getInstance() {
        if (instance == null)
            instance = new UserMeetingsRepo();
        return instance;
    }

    public LiveData<ArrayList<LiveData<GroupMeeting>>> getGroupsMeetings(){
        MutableLiveData<ArrayList<LiveData<GroupMeeting>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(groupsMeetings);
        FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getInstance().getId()).child("Meetings").child("Group").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String group = snapshot.getValue(String.class);
                FirebaseDatabase.getInstance().getReference().child("Groups").child(group).child("Meetings").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GroupMeeting meeting = snapshot.getValue(GroupMeeting.class);
                        MutableLiveData<GroupMeeting> meetingMutableLiveData = new MutableLiveData<>();
                        meetingMutableLiveData.setValue(meeting);
                        int i = sortGroupMeeting(meeting);
                        groupsMeetings.add(i,meetingMutableLiveData);
                        mutableLiveData.postValue(groupsMeetings);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
       // mutableLiveData.
        return mutableLiveData;
    }

    public LiveData<ArrayList<LiveData<Meeting>>> getPublicMeetings(){
        MutableLiveData<ArrayList<LiveData<Meeting>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(publicMeetings);
        FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getInstance().getId()).child("Meetings").child("Public").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                FirebaseDatabase.getInstance().getReference().child("Meetings").child("Public").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Meeting meeting = snapshot.getValue(Meeting.class);
                            MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                            meetingMutableLiveData.setValue(meeting);
                            Log.d(TAG, "onDataChange: --------------------------------------------------------" + meeting.toString());
                            int i = sortPublicMeeting(meeting);
                            publicMeetings.add(i, meetingMutableLiveData);
                            mutableLiveData.postValue(publicMeetings);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        // mutableLiveData.
        return mutableLiveData;
    }

    private int sortPublicMeeting(Meeting m){
        if (publicHash.containsKey(m.getId())) {
            publicMeetings.remove(publicHash.get(m.getId()));
        }
        if (publicMeetings.isEmpty()){
            return 0;
        }
        for (int i = 0; i < publicMeetings.size(); i++) {
            if (publicMeetings.get(i).getValue().getMillis() > m.getMillis()){
                publicHash.put(m.getId(),i);
               return i;
            }
        }
        return publicMeetings.size();
    }

    private int sortGroupMeeting(GroupMeeting m){
        if (groupHash.containsKey(m.getId())) {
            groupsMeetings.remove(groupHash.get(m.getId()));
        }
        if (groupsMeetings.isEmpty()){
            return 0;
        }
        for (int i = 0; i < groupsMeetings.size(); i++) {
            if (groupsMeetings.get(i).getValue().getMillis() > m.getMillis()){
                groupHash.put(m.getId(),i);
                return i;
            }
        }
        return groupsMeetings.size();
    }
}
