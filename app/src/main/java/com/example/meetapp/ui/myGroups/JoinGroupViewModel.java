package com.example.meetapp.ui.myGroups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.SearchGroupFirebase;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

public class JoinGroupViewModel extends ViewModel {

    private MutableLiveData<ArrayList<MutableLiveData<Group>>> result;

    public void search(String name){
        this.result = SearchGroupFirebase.searchGroups(name);
    }

    public LiveData<ArrayList<MutableLiveData<Group>>> getResult() {
        return result;
    }
}