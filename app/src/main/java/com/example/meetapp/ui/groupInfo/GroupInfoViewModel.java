package com.example.meetapp.ui.groupInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.firebaseActions.SingleGroupRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupInfoViewModel extends ViewModel {

    private MutableLiveData<Group> groupMutableLiveData;
    private MutableLiveData<ArrayList<MutableLiveData<User>>> membersMutableLiveData;
    private GroupsMembersRepo groupsMembersRepo;
    private String groupId;

    public void init(String groupId){
        this.groupId = groupId;
        this.groupMutableLiveData = new MutableLiveData<>();
        SingleGroupRepo singleGroupRepo = new SingleGroupRepo();
        groupMutableLiveData = singleGroupRepo.getGroupData(groupId);
        groupsMembersRepo = new GroupsMembersRepo(groupId);
        membersMutableLiveData = groupsMembersRepo.getMembers();
    }

    public void setGroupMutableLiveData(MutableLiveData<Group> groupMutableLiveData) {
        this.groupMutableLiveData = groupMutableLiveData;
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