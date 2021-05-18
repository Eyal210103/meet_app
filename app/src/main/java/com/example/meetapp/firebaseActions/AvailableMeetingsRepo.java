package com.example.meetapp.firebaseActions;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.callbacks.OnCompleteAction;
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

import javax.inject.Singleton;

@Singleton
public class AvailableMeetingsRepo {

    private final MutableLiveData<ArrayList<MutableLiveData<Meeting>>> publicMeetingsMutableLiveData = new MutableLiveData<>();
    private final HashMap<String,String> meetingIdToGroupId = new HashMap<>();
    private final HashMap<String,Integer> publicMeetingToIndexHash = new HashMap<>();
    private final ArrayList<MutableLiveData<Meeting>> publicMeetings = new ArrayList<>();
    private long todayMillis;
    private Context listener;

    private static AvailableMeetingsRepo instance =  null;

    private AvailableMeetingsRepo() {
    }

    public static AvailableMeetingsRepo getInstance() {
        if (instance == null) {
            instance = new AvailableMeetingsRepo();
            instance.loadPublicMeetings();
            instance.loadPublicGroupMeetings();
        }
        return instance;
    }

    public static AvailableMeetingsRepo getInstance(Context context){
        if (instance == null) {
            instance = new AvailableMeetingsRepo();
            instance.loadPublicMeetings();
            instance.loadPublicGroupMeetings();
        }
        instance.listener = context;
        return instance;
    }

    private void loadPublicMeetings(){
        publicMeetingsMutableLiveData.setValue(publicMeetings);
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.PUBLIC_MEETINGS_CHILDES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Meeting meeting = snapshot.getValue(Meeting.class);
                todayMillis = Calendar.getInstance().getTime().getTime();
                if (meeting != null && meeting.getMillis() > todayMillis) {
                    MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                    meetingMutableLiveData.setValue(meeting);
                    publicMeetingToIndexHash.put(meeting.getId(), publicMeetings.size());
                    publicMeetings.add(meetingMutableLiveData);
                    publicMeetingsMutableLiveData.postValue(publicMeetings);
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                int i = publicMeetingToIndexHash.get(snapshot.getKey());
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
    }

    private void loadPublicGroupMeetings(){
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUP_MEETINGS_CHILDES).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String groupId = snapshot.getValue(String.class);
                String meetingId = snapshot.getKey();
                loadSinglePublicGroupMeetingData(groupId,meetingId);

                if (listener != null){
                    OnCompleteAction onCompleteAction = (OnCompleteAction)listener;
                    onCompleteAction.OnComplete();
                    listener = null;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSinglePublicGroupMeetingData(String groupId, String meetingId){
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(groupId).child(FirebaseTags.MEETINGS_CHILDES).child(meetingId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Meeting meeting = snapshot.getValue(Meeting.class);
                todayMillis = Calendar.getInstance().getTime().getTime();
                if (meeting != null && meeting.getMillis() > todayMillis) {
                    MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                    meetingMutableLiveData.setValue(meeting);
                    publicMeetingToIndexHash.put(meeting.getId(), publicMeetings.size()-1);
                    publicMeetings.add(meetingMutableLiveData);
                    publicMeetingsMutableLiveData.postValue(publicMeetings);
                    meetingIdToGroupId.put(meetingId,groupId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<ArrayList<MutableLiveData<Meeting>>> getPublicMeetings(){
        return publicMeetingsMutableLiveData;
    }

    public static LiveData<ArrayList<User>> getWhoComing(String mId){
        ArrayList<User> arrayList = new ArrayList<User>();
        MutableLiveData<ArrayList<User>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(arrayList);
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.PUBLIC_MEETINGS_CHILDES).child(mId)
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
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
        return mutableLiveData;
    }

    public HashMap<String, String> getMeetingIdToGroupId() {
        return meetingIdToGroupId;
    }
}
