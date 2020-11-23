package com.example.meetapp.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<Group>>> groupsList = null;
    HashMap<String, MutableLiveData<Group>> groupsMap = null;
    LiveData<ArrayList<LiveData<Meeting>>> meetingsList = null;
    LiveData<ArrayList<LiveData<GroupMeeting>>> groupsMeetingsList = null;

    public MainActivityViewModel() {
        super();
        groupsList = UserGroupsRepo.getInstance().getGroups();
        groupsMap = UserGroupsRepo.getInstance().getHashMapGroups();
    }

//    public void init(@NonNull Context context) {
//        if (list == null) {
//            list = UserGroupsRepo.getInstance().getGroups();
//            map = UserGroupsRepo.getInstance().getHashMapGroups();
//        }
//    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getGroups() {
        return groupsList;
    }

    public HashMap<String, MutableLiveData<Group>> getGroupsMap() {
        return groupsMap;
    }
}