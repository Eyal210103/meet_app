package com.example.meetapp.ui.groupInfo.groupSettings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupSettingsRepo;
import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupSettingsViewModel extends ViewModel {
    private LiveData<ArrayList<MutableLiveData<User>>> paddingUsers;
    private GroupSettingsRepo groupSettingsRepo;
    private LiveData<Group> group;
    private LiveData<ArrayList<String>> managers;
    private LiveData<ArrayList<MutableLiveData<User>>> members;

    public GroupSettingsViewModel() {
        super();
    }
    public void init(MutableLiveData<Group> group , String id){
        groupSettingsRepo = new GroupSettingsRepo(id);
        paddingUsers = groupSettingsRepo.getWaitingUsers();
        this.group = group;
        managers = groupSettingsRepo.getManagers();
        GroupsMembersRepo groupsMembersRepo = new GroupsMembersRepo(id);
        members = groupsMembersRepo.getMembers();
    }

    public LiveData<ArrayList<MutableLiveData<User>>> getPaddingUsers() {
        return paddingUsers;
    }

    public LiveData<ArrayList<String>> getManagers() {
        return managers;
    }

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<ArrayList<MutableLiveData<User>>> getMembers() {
        return members;
    }

    public void approveUser(int position){
        groupSettingsRepo.approveUser(paddingUsers.getValue().get(position).getValue().getId());
    }
    public void rejectUser(int position){
        groupSettingsRepo.removeUser(paddingUsers.getValue().get(position).getValue().getId());
    }
}