package com.example.meetapp.ui.groupInfo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.model.Group;

public class GroupInfoViewModel extends ViewModel {

    MutableLiveData<Group> groupMutableLiveData;

    public void init(@NonNull Context context, String groupId){
        this.groupMutableLiveData = null;
    }

    public LiveData<Group> getGroupMutableLiveData() {
        return groupMutableLiveData;
    }

    public void setGroup(MutableLiveData<Group> groupMutableLiveData) {
        this.groupMutableLiveData = groupMutableLiveData;
    }
}