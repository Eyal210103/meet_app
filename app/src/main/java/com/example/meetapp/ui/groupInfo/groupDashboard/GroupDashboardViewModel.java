package com.example.meetapp.ui.groupInfo.groupDashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.firebaseActions.LastMessageRepo;
import com.example.meetapp.model.User;
import com.example.meetapp.model.Message;

import java.util.ArrayList;

public class GroupDashboardViewModel extends ViewModel {

    private MutableLiveData<Message> lastMessage;
    LiveData<ArrayList<MutableLiveData<User>>> membersMutableLiveData;


    public void init(String groupId){
        LastMessageRepo lastMessageRepo =new LastMessageRepo(groupId);
        lastMessage = lastMessageRepo.getMessage();
        GroupsMembersRepo groupsMembersRepo = new GroupsMembersRepo(groupId);
        membersMutableLiveData = groupsMembersRepo.getMembers();
    }

    public LiveData<Message> getLastMessage() {
        return lastMessage;
    }

    public LiveData<ArrayList<MutableLiveData<User>>> getMembersMutableLiveData() {
        return membersMutableLiveData;
    }

    public void setMembersMutableLiveData(LiveData<ArrayList<MutableLiveData<User>>> membersMutableLiveData) {
        this.membersMutableLiveData = membersMutableLiveData;
    }
}