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
    LiveData<HashMap<String, ArrayList<LiveData<Meeting>>>> meetingsMap = null;
    HashMap<String,String> idsOfGroupMeetingsToGroup = null;

    public MainActivityViewModel() {
        super();
        groupsList = UserGroupsRepo.getInstance().getGroups();
        groupsMap = UserGroupsRepo.getInstance().getHashMapGroups();
        meetingsMap = UserMeetingsRepo.getInstance().getAllMeetings();
        idsOfGroupMeetingsToGroup = UserMeetingsRepo.getInstance().getIdsOfGroupMeetingsToGroup();
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

    public LiveData<HashMap<String, ArrayList<LiveData<Meeting>>>> getMeetingsMap() {
        return meetingsMap;
    }

    public HashMap<String, String> getIdsOfGroupMeetingsToGroup() {
        return idsOfGroupMeetingsToGroup;
    }
//    public LiveData<ArrayList<LiveData<GroupMeeting>>> getGroupsMeetingsList() {
//        return groupsMeetingsList;
//    }

    public void leaveGroup(String id) {
        UserGroupsRepo.getInstance().leaveGroup(id);
    }
}