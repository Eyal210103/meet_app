package com.example.meetapp.ui.groupInfo.groupSettings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupMembersRepo;
import com.example.meetapp.firebaseActions.GroupSettingsRepo;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupSettingsViewModel extends ViewModel {
    private LiveData<ArrayList<LiveData<User>>> paddingUsers;
    private GroupSettingsRepo groupSettingsRepo;
    private LiveData<Group> group;
    private LiveData<ArrayList<String>> managers;
    private LiveData<ArrayList<LiveData<User>>> members;

    public void init(LiveData<Group> group , String id){
        groupSettingsRepo = new GroupSettingsRepo(id);
        paddingUsers = groupSettingsRepo.getWaitingUsers();
        this.group = group;
        managers = groupSettingsRepo.getManagers();

        GroupMembersRepo groupMembersRepo = new GroupMembersRepo(id);
        members = groupMembersRepo.getMembers();
    }

    public LiveData<ArrayList<LiveData<User>>> getPendingUsers() {
        return paddingUsers;
    }

    public LiveData<ArrayList<String>> getManagers() {
        return managers;
    }

    public LiveData<Group> getGroup() {
        return group;
    }

    public LiveData<ArrayList<LiveData<User>>> getMembers() {
        return members;
    }

    public void approveUser(String id){
        groupSettingsRepo.approveUser(id);
    }
    public void rejectUser(String id){
        groupSettingsRepo.removeWaitingUser(id);
    }

    public void removeUser(String id){
        groupSettingsRepo.removeUser(id);
    }

    public void addManager(String id){
        groupSettingsRepo.addManager(id);
    }
}