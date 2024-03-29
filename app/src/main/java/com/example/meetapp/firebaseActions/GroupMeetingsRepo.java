package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GroupMeetingsRepo {

    private final HashMap<String, ArrayList<LiveData<GroupMeeting>>> allMeetings;
    private final MutableLiveData<HashMap<String, ArrayList<LiveData<GroupMeeting>>>> mutableLiveData;
    private final HashMap<String, LiveData<HashMap<String, LiveData<User>>>> whoComing;
    private final MutableLiveData<HashMap<String, LiveData<HashMap<String, LiveData<User>>>>> mutableLiveWhoComing;
    private final HashMap<String,String> meetingIdToStringDate;
    private final String id;
    private final MutableLiveData<GroupMeeting> closesMeeting;
    private GroupMeeting closesMeetingDAO;

    public static final long TIME_BEFORE = 3600000;

    ChildEventListener listener;

    public GroupMeetingsRepo(String id) {
        this.closesMeeting = new MutableLiveData<>();
        this.allMeetings = new HashMap<>();
        this.mutableLiveData = new MutableLiveData<>();
        this.whoComing = new HashMap<>();
        this.mutableLiveWhoComing = new MutableLiveData<>();
        meetingIdToStringDate = new HashMap<>();
        this.id = id;
        this.loadMeetings();
    }

    /**
     * a method that responsible to add database listening for group meetings
     */
    private void loadMeetings() {
        mutableLiveData.setValue(allMeetings);

        this.listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GroupMeeting meeting = snapshot.getValue(GroupMeeting.class);
                Calendar calendar = Calendar.getInstance();
                if (calendar.getTime().getTime() - TIME_BEFORE < meeting.getMillis()) {
                    MutableLiveData<GroupMeeting> meetingMutableLiveData = new MutableLiveData<>();
                    meetingMutableLiveData.setValue(meeting);
                    assert meeting != null;
                    if (!allMeetings.containsKey(meeting.getDateString())) {
                        allMeetings.put(meeting.getDateString(), new ArrayList<>());
                    }
                    ArrayList<LiveData<GroupMeeting>> temp = allMeetings.get(meeting.getDateString());
                    boolean inserted = false;
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).getValue().getId().equals(meeting.getId())) {
                            temp.set(i, meetingMutableLiveData);
                            inserted = true;
                            break;
                        }
                    }
                    if (!inserted) {
                        temp.add(meetingMutableLiveData);
                    }
                    meetingIdToStringDate.put(meeting.getId(), meeting.getDateString());

                    allMeetings.put(meeting.getDateString(), temp);
                    mutableLiveData.postValue(allMeetings);

                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                    HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.child(FirebaseTags.WHO_COMING_CHILDES).getValue(genericTypeIndicator);
                    loadWhoComing(hashMap, meeting.getId());

                    if (closesMeetingDAO == null || closesMeetingDAO != null && closesMeetingDAO.getMillis() > meeting.getMillis()) {
                        closesMeeting.setValue(meeting);
                        closesMeetingDAO = meeting;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GroupMeeting meeting = snapshot.getValue(GroupMeeting.class);
                MutableLiveData<GroupMeeting> meetingMutableLiveData = new MutableLiveData<>();
                meetingMutableLiveData.setValue(meeting);
                assert meeting != null;
                if (!allMeetings.containsKey(meeting.getDateString())) {
                    allMeetings.put(meeting.getDateString(), new ArrayList<>());
                }
                ArrayList<LiveData<GroupMeeting>> temp = allMeetings.get(meeting.getDateString());
                boolean inserted = false;
                for (int i = 0; i < temp.size(); i++) {
                    if (temp.get(i).getValue().getId().equals(meeting.getId())) {
                        temp.set(i, meetingMutableLiveData);
                        inserted = true;
                        break;
                    }
                }
                if (!inserted) {
                    temp.add(meetingMutableLiveData);
                }
                allMeetings.put(meeting.getDateString(), temp);
                mutableLiveData.postValue(allMeetings);

                GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.child(FirebaseTags.WHO_COMING_CHILDES).getValue(genericTypeIndicator);
                loadWhoComing(hashMap, meeting.getId());

                if (closesMeeting.getValue().getId().equals(meeting.getId())) {
                    closesMeeting.postValue(meeting);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String meetingId = snapshot.getKey();
                if (meetingIdToStringDate.containsKey(meetingId)){
                    String date = meetingIdToStringDate.get(meetingId);
                    int i = 0;
                    for (LiveData<GroupMeeting> liveData:allMeetings.get(date)) {
                        if (liveData.getValue().getId().equals(meetingId)){
                            allMeetings.get(date).remove(i);
                            mutableLiveData.postValue(allMeetings);
                            meetingIdToStringDate.remove(meetingId);// to observers
                            return;
                        }
                        i++;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.id).child(FirebaseTags.MEETINGS_CHILDES).addChildEventListener(this.listener);
    }

    /**
     * @return mutableLiveData
     */
    public LiveData<HashMap<String, ArrayList<LiveData<GroupMeeting>>>> getMeetings() {
        return mutableLiveData;
    }

    /**
     * a method that responsible to add database listening for a who comes to a certain meeting
     * @param members represents a hashmap of users as we get it from firebase
     * @param mId represents the meeting id
     */
    private void loadWhoComing(HashMap<String, String> members, String mId) {
        if (!whoComing.containsKey(mId)) {
            MutableLiveData<HashMap<String, LiveData<User>>> temp = new MutableLiveData<>();
            temp.setValue(new HashMap<>());
            whoComing.put(mId, temp);
        }
        LiveData<HashMap<String, LiveData<User>>> curr = whoComing.get(mId);
        if (members!= null) {
            for (String s : members.values()) {
                if (!curr.getValue().containsKey(s)) {
                    curr.getValue().put(s, putUserData(s));
                }
            }
        }
        whoComing.put(mId, curr);
        mutableLiveWhoComing.postValue(whoComing);
    }

    /**
     *  a method that responsible to add database listening for a certain user who comes to a certain meeting
     * @param id  represents the user's UId
     * @return LiveData of the user
     */
    private LiveData<User> putUserData(String id) {
        Query reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(id);
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMutableLiveData.postValue(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return userMutableLiveData;
    }

    /**
     * @return closesMeeting
     */
    public LiveData<GroupMeeting> getClosesMeeting() {
        return closesMeeting;
    }

    public void detachListener() {
        if (this.listener != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.id).child(FirebaseTags.MEETINGS_CHILDES).removeEventListener(this.listener);
        }
    }

}
