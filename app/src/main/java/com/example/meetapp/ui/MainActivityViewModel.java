package com.example.meetapp.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.firebaseActions.UserMeetingsRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<Group>>> groupsList = null;
    HashMap<String, MutableLiveData<Group>> groupsMap = null;
    LiveData<HashMap<String,LiveData<Meeting>>> meetingsList = null;
    //LiveData<ArrayList<LiveData<GroupMeeting>>> groupsMeetingsList = null;

    public MainActivityViewModel() {
        super();
        groupsList = UserGroupsRepo.getInstance().getGroups();
        groupsMap = UserGroupsRepo.getInstance().getHashMapGroups();
        meetingsList = UserMeetingsRepo.getInstance().getAllMeetings();
    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getGroups() {
        return groupsList;
    }

    public HashMap<String, MutableLiveData<Group>> getGroupsMap() {
        return groupsMap;
    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getGroupsList() {
        return groupsList;
    }

    public LiveData<HashMap<String, LiveData<Meeting>>> getMeetingsList() {
        return meetingsList;
    }

//    public LiveData<ArrayList<LiveData<GroupMeeting>>> getGroupsMeetingsList() {
//        return groupsMeetingsList;
//    }

    public void leaveGroup(String id) {
        UserGroupsRepo.getInstance().leaveGroup(id);
    }
}