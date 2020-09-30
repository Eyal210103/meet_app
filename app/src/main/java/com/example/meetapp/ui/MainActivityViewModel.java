package com.example.meetapp.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.model.Group;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<Group>>> list = null;
    HashMap<String, MutableLiveData<Group>> map = null;

    public MainActivityViewModel() {
        super();
        list = UserGroupsRepo.getInstance().getGroups();
        map = UserGroupsRepo.getInstance().getHashMapGroups();
    }

//    public void init(@NonNull Context context) {
//        if (list == null) {
//            list = UserGroupsRepo.getInstance().getGroups();
//            map = UserGroupsRepo.getInstance().getHashMapGroups();
//        }
//    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getGroups() {
        return list;
    }

    public HashMap<String, MutableLiveData<Group>> getGroupsMap() {
        return map;
    }
}