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

import java.util.ArrayList;
import java.util.HashMap;

public class UserMeetingsRepo {

    private final HashMap<String, ArrayList<LiveData<Meeting>>> allMeetings;
    private final MutableLiveData<HashMap<String, ArrayList<LiveData<Meeting>>>> mutableLiveData;
    private final HashMap<String,String> idsOfGroupMeetingsToGroup;
    private final HashMap<String,String> meetingIdToStringDate;

    private final MutableLiveData<ArrayList<MutableLiveData<Meeting>>> meetingsListMutableLiveData = new MutableLiveData<>();
    private final ArrayList<MutableLiveData<Meeting>> userAllMeetingsList = new ArrayList<>();


    private static UserMeetingsRepo instance = null;

    private UserMeetingsRepo() {
        allMeetings = new HashMap<>();
        mutableLiveData = new MutableLiveData<>();
        idsOfGroupMeetingsToGroup = new HashMap<>();
        meetingIdToStringDate = new HashMap<>();
        meetingsListMutableLiveData.setValue(userAllMeetingsList);
    }

    public static UserMeetingsRepo getInstance() {
        if (instance == null) {
            instance = new UserMeetingsRepo();
            instance.loadPublicMeetings();
            instance.loadGroupsMeetings();
        }
        return instance;
    }

    public LiveData<HashMap<String, ArrayList<LiveData<Meeting>>>> getAllMeetings() {
        mutableLiveData.setValue(allMeetings);
        return mutableLiveData;
    }

    private void loadGroupsMeetings() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance().getId()).child(FirebaseTags.GROUP_MEETINGS_CHILDES)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String meetingId = snapshot.getKey();
                String groupId = snapshot.getValue(String.class);

                FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(groupId).child(FirebaseTags.MEETINGS_CHILDES).child(meetingId)
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            GroupMeeting meeting = snapshot.getValue(GroupMeeting.class);
                            MutableLiveData<Meeting> meetingMutableLiveData = new MutableLiveData<>();
                            meetingMutableLiveData.setValue(meeting);
                            if (!allMeetings.containsKey(meeting.getDateString())){ // put new array
                                allMeetings.put(meeting.getDateString(),new ArrayList<>());
                            }

                            ArrayList<LiveData<Meeting>> temp = allMeetings.get(meeting.getDateString());
                            boolean inserted = false;
                            for (int i = 0; i < temp.size(); i++) { // check if update is needed
                                if (temp.get(i).getValue().getId().equals(meeting.getId())){
                                    temp.set(i,meetingMutableLiveData);
                                    inserted = true;
                                    break;
                                }
                            }
                            if (!inserted){ // if not exist -> add
                                temp.add(meetingMutableLiveData);
                                idsOfGroupMeetingsToGroup.put(meetingId,groupId);
                            }
                            meetingIdToStringDate.put(meetingId,meeting.getDateString());
                            allMeetings.put(meeting.getDateString(),temp); // to observers

                            mutableLiveData.postValue(allMeetings);

                            userAllMeetingsList.add(meetingMutableLiveData);
                            meetingsListMutableLiveData.postValue(userAllMeetingsList); // to observers
                        }
                        else {
                            FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance()
                                    .getId()).child(FirebaseTags.GROUP_MEETINGS_CHILDES).child(meetingId).removeValue();
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
                String meetingId = snapshot.getKey();
                if (meetingIdToStringDate.containsKey(meetingId)){
                    String date = meetingIdToStringDate.get(meetingId);
                    int i = 0;
                    for (LiveData<Meeting> liveData:allMeetings.get(date)) {
                        if (liveData.getValue().getId().equals(meetingId)){
                            allMeetings.get(date).remove(i);
                            mutableLiveData.postValue(allMeetings); // to observers
                            idsOfGroupMeetingsToGroup.remove(meetingId);
                            meetingIdToStringDate.remove(meetingId);
                            break;
                        }
                        i++;
                    }
                }
                for (int i = 0; i < userAllMeetingsList.size(); i++) {
                    if (userAllMeetingsList.get(i).getValue().getId().equals(meetingId)){
                        userAllMeetingsList.remove(i);
                        meetingsListMutableLiveData.postValue(userAllMeetingsList); // to observers
                        break;
                    }
                }
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
                            if (!allMeetings.containsKey(meeting.getDateString())){
                                allMeetings.put(meeting.getDateString(),new ArrayList<>());
                            }
                            ArrayList<LiveData<Meeting>> temp = allMeetings.get(meeting.getDateString());
                            boolean inserted = false;
                            for (int i = 0; i < temp.size(); i++) {
                                if (temp.get(i).getValue().getId().equals(meeting.getId())){
                                    temp.set(i,meetingMutableLiveData);
                                    inserted = true;
                                    break;
                                }
                            }
                            if (!inserted){
                                temp.add(meetingMutableLiveData);
                            }
                            userAllMeetingsList.add(meetingMutableLiveData);
                            meetingsListMutableLiveData.postValue(userAllMeetingsList);

                            meetingIdToStringDate.replace(meeting.getId(),meeting.getDateString());
                            allMeetings.put(meeting.getDateString(),temp);
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
                String meetingId = snapshot.getKey();
                if (meetingIdToStringDate.containsKey(meetingId)){
                    String date = meetingIdToStringDate.get(meetingId);
                    int i = 0;
                    for (LiveData<Meeting> liveData:allMeetings.get(date)) {
                        if (liveData.getValue().getId().equals(meetingId)){
                            allMeetings.get(date).remove(i);
                            mutableLiveData.postValue(allMeetings); // to observers
                            idsOfGroupMeetingsToGroup.remove(meetingId);
                            break;
                        }
                        i++;
                    }
                }
                for (int i = 0; i < userAllMeetingsList.size(); i++) {
                    if (userAllMeetingsList.get(i).getValue().getId().equals(meetingId)){
                        userAllMeetingsList.remove(i);
                        meetingsListMutableLiveData.postValue(userAllMeetingsList); // to observers
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public MutableLiveData<ArrayList<MutableLiveData<Meeting>>> getMeetingsListMutableLiveData() {
        return meetingsListMutableLiveData;
    }

    public HashMap<String, String> getIdsOfGroupMeetingsToGroup() {
        return idsOfGroupMeetingsToGroup;
    }
}
