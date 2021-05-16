package com.example.meetapp.ui.groupInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupMeetingsRepo;
import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.firebaseActions.LastMessageRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.Message;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInfoViewModel extends ViewModel {

    private String groupId;
    private GroupsMembersRepo groupsMembersRepo;
    private GroupMeetingsRepo groupMeetingsRepo;
    private LastMessageRepo lastMessageRepo;

    private LiveData<Group> groupMutableLiveData;
    private LiveData<ArrayList<MutableLiveData<User>>> membersMutableLiveData;
    private LiveData<HashMap<String, ArrayList<LiveData<GroupMeeting>>>> meetingsLiveData;
    private LiveData<GroupMeeting> closesMeeting;
    private LiveData<Message> lastMessage;

    public void init(LiveData<Group> groupLiveData , String id){
        this.groupId = id;
        this.groupMutableLiveData = groupLiveData;
        groupsMembersRepo = new GroupsMembersRepo(groupId);
        membersMutableLiveData = groupsMembersRepo.getMembers();
        groupMeetingsRepo = new GroupMeetingsRepo(groupId);
        meetingsLiveData = groupMeetingsRepo.getMeeting();
        closesMeeting = groupMeetingsRepo.getClosesMeeting();
        lastMessageRepo = new LastMessageRepo(id);
        lastMessage = lastMessageRepo.getMessage();
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
        groupMeetingsRepo.detachListener();
        lastMessageRepo.detachListener();
    }

    public LiveData<HashMap<String, ArrayList<LiveData<GroupMeeting>>>> getMeetings() {
        return meetingsLiveData;
    }

    public LiveData<Message> getLastMessage() {
        return lastMessage;
    }
}