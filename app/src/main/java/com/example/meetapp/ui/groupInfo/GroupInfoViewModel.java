package com.example.meetapp.ui.groupInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupInfoViewModel extends ViewModel {

    MutableLiveData<Group> groupMutableLiveData;
    MutableLiveData<ArrayList<MutableLiveData<User>>> membersMutableLiveData;
    GroupsMembersRepo groupsMembersRepo;

    public void init(String groupId){
        this.groupMutableLiveData = new MutableLiveData<>();
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

}