package com.example.meetapp.ui.socialMenu.myGroups;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.model.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyGroupsViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<Group>>> map = null;

    public void init(@NonNull MyGroupsFragment context) {
            map = UserGroupsRepo.getInstance(context).getGroups();
    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getGroups() {
        return map;
    }
}
