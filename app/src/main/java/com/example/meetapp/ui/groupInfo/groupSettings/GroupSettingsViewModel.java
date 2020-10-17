package com.example.meetapp.ui.groupInfo.groupSettings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupSettingsRepo;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class GroupSettingsViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<User>>> paddingUsers;

    public GroupSettingsViewModel() {
        super();
    }
    public void init(String id){
        GroupSettingsRepo groupSettingsRepo = new GroupSettingsRepo(id);
        paddingUsers = groupSettingsRepo.getWaitingUsers();
    }

    public LiveData<ArrayList<MutableLiveData<User>>> getPaddingUsers() {
        return paddingUsers;
    }
}