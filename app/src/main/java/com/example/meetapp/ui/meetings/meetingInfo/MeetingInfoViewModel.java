package com.example.meetapp.ui.meetings.meetingInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.FirebaseTags;
import com.example.meetapp.firebaseActions.MeetingInfoRepo;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MeetingInfoViewModel extends ViewModel {

    private LiveData<ArrayList<User>> users;
    private LiveData<Meeting> publicM;
    private LiveData<GroupMeeting> groupM;
    private String groupId;
    private LiveData<Group> groupData;


    public void init(String id, String groupId, String type) {
        this.groupId = groupId;
        MeetingInfoRepo meetingInfoRepo = new MeetingInfoRepo(id);
        users = meetingInfoRepo.loadWhoComing();
        if (type.equals(Const.MEETING_TYPE_PUBLIC)) {
            publicM = meetingInfoRepo.loadPublicMeeting();
            users = meetingInfoRepo.loadWhoComing();
            groupM = null;
        } else if (type.equals(Const.MEETING_TYPE_GROUP)) {
            groupM = meetingInfoRepo.loadGroupMeeting(groupId);
            users = meetingInfoRepo.loadWhoComing(groupId);
            groupData = getGroupData(groupId);
            publicM = null;
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public LiveData<ArrayList<User>> getUsers() {
        return users;
    }

    public LiveData<Meeting> getPublicM() {
        return publicM;
    }

    public LiveData<GroupMeeting> getGroupM() {
        return groupM;
    }

    public LiveData<Group> getGroupData() {
        return groupData;
    }

    private MutableLiveData<Group> getGroupData(String key) {
        MutableLiveData<Group> mutableLiveData = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mutableLiveData.setValue(snapshot.getValue(Group.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        return mutableLiveData;
    }

}