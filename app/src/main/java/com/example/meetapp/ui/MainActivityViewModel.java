package com.example.meetapp.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.UserGroupsRepo;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

public class MainActivityViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<Group>>> map = null;

    public void init(@NonNull Context context) {
        map = UserGroupsRepo.getInstance(context).getGroups();
    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getGroups() {
        return map;
    }
}