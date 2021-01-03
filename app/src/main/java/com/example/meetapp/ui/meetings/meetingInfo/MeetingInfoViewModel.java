package com.example.meetapp.ui.meetings.meetingInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.MeetingInfoRepo;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;

public class MeetingInfoViewModel extends ViewModel {

    private LiveData<ArrayList<User>> users;
    private LiveData<Meeting> publicM;
    private LiveData<GroupMeeting> groupM;


    public void init(String id, String groupId,  String type){
        MeetingInfoRepo meetingInfoRepo = new MeetingInfoRepo(id);
        users = meetingInfoRepo.loadWhoComing();
        if (type.equals("Public")) {
            publicM = meetingInfoRepo.loadPublicMeeting();
            groupM = null;
        }else {
            groupM = meetingInfoRepo.loadGroupMeeting(groupId);
            publicM = null;
        }
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
}