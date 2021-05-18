package com.example.meetapp.ui.groupInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupMeetingsRepo;
import com.example.meetapp.firebaseActions.GroupMembersRepo;
import com.example.meetapp.firebaseActions.LastMessageRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.Message;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupInfoViewModel extends ViewModel {

    private String groupId;
    private GroupMembersRepo groupMembersRepo;
    private GroupMeetingsRepo groupMeetingsRepo;
    private LastMessageRepo lastMessageRepo;

    private LiveData<Group> groupMutableLiveData;
    private LiveData<ArrayList<LiveData<User>>> membersMutableLiveData;
    private LiveData<HashMap<String, ArrayList<LiveData<GroupMeeting>>>> meetingsLiveData;
    private LiveData<GroupMeeting> closesMeeting;
    private LiveData<Message> lastMessage;

    public void init(LiveData<Group> groupLiveData , String id){
        this.groupId = id;
        this.groupMutableLiveData = groupLiveData;

        groupMembersRepo = new GroupMembersRepo(groupId);
        membersMutableLiveData = groupMembersRepo.getMembers();

        groupMeetingsRepo = new GroupMeetingsRepo(groupId);
        meetingsLiveData = groupMeetingsRepo.getMeetings();
        closesMeeting = groupMeetingsRepo.getClosesMeeting();

        lastMessageRepo = new LastMessageRepo(id);
        lastMessage = lastMessageRepo.getMessage();
    }

    public LiveData<Group> getGroup() {
        return groupMutableLiveData;
    }

    public LiveData<ArrayList<LiveData<User>>> getMembersLiveData() {
        return membersMutableLiveData;
    }

    public LiveData<GroupMeeting> getClosesMeeting() {
        return closesMeeting;
    }

    public String getGroupId() {
        return groupId;
    }

    public void detach() {
        groupMembersRepo.detachListener();
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