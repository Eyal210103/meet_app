package com.example.meetapp.ui.groupInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupMeetingsRepo;
import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInfoViewModel extends ViewModel {

    private MutableLiveData<Group> groupMutableLiveData;
    private MutableLiveData<ArrayList<MutableLiveData<User>>> membersMutableLiveData;
    private LiveData<HashMap<String, ArrayList<LiveData<GroupMeeting>>>> meetingsLiveData;
    private String groupId;
    private GroupsMembersRepo groupsMembersRepo;
    private GroupMeetingsRepo groupMeetingsRepo;
    private LiveData<GroupMeeting> closesMeeting;

    public void init(MutableLiveData<Group> groupMutableLiveData , String id){
        this.groupId = id;
        this.groupMutableLiveData = groupMutableLiveData;
        groupsMembersRepo = new GroupsMembersRepo(groupId);
        membersMutableLiveData = groupsMembersRepo.getMembers();
        groupMeetingsRepo = new GroupMeetingsRepo(groupId, groupsMembersRepo);
        meetingsLiveData = groupMeetingsRepo.getMeeting();
        closesMeeting = groupMeetingsRepo.getClosesMeeting();
    }

    public LiveData<Group> getGroup() {
        return groupMutableLiveData;
    }

    public LiveData<ArrayList<MutableLiveData<User>>> getMembersLiveData() {
        return membersMutableLiveData;
    }

    public LiveData<GroupMeeting> getClosesMeeting() {
        return closesMeeting;
    }

    public String getGroupId() {
        return groupId;
    }

    public void detach() {
        groupsMembersRepo.detachListener();
    }

    public LiveData<HashMap<String, ArrayList<LiveData<GroupMeeting>>>> getMeetings() {
        return meetingsLiveData;
    }
}