package com.example.meetapp.ui.groupInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupInfoViewModel extends ViewModel {

    private MutableLiveData<Group> groupMutableLiveData;
    private MutableLiveData<ArrayList<MutableLiveData<User>>> membersMutableLiveData;
    private String groupId;

    public void init(MutableLiveData<Group> groupMutableLiveData , String id){
        this.groupId = id;
        this.groupMutableLiveData = groupMutableLiveData;
        GroupsMembersRepo groupsMembersRepo = new GroupsMembersRepo(groupId);
        membersMutableLiveData = groupsMembersRepo.getMembers();
    }

    public MutableLiveData<Group> getGroup() {
        return groupMutableLiveData;
    }

    public LiveData<Group> getGroupMutableLiveData() {
        return groupMutableLiveData;
    }

    public LiveData<ArrayList<MutableLiveData<User>>> getMembersMutableLiveData() {
        return membersMutableLiveData;
    }

    public void setGroup(MutableLiveData<Group> groupMutableLiveData) {
        this.groupMutableLiveData = groupMutableLiveData;
    }

    public String getGroupId() {
        return groupId;
    }
}