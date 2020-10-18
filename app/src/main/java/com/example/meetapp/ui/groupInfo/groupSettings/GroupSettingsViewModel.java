package com.example.meetapp.ui.groupInfo.groupSettings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupSettingsRepo;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupSettingsViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<User>>> paddingUsers;
    GroupSettingsRepo groupSettingsRepo;

    public GroupSettingsViewModel() {
        super();
    }
    public void init(String id){
        groupSettingsRepo = new GroupSettingsRepo(id);
        paddingUsers = groupSettingsRepo.getWaitingUsers();
    }

    public LiveData<ArrayList<MutableLiveData<User>>> getPaddingUsers() {
        return paddingUsers;
    }

    public void approveUser(int position){
        groupSettingsRepo.approveUser(paddingUsers.getValue().get(position).getValue().getId());
    }
    public void rejectUser(int position){
        groupSettingsRepo.removeUser(paddingUsers.getValue().get(position).getValue().getId());
    }
}