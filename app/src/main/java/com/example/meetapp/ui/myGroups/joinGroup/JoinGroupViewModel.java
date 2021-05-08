package com.example.meetapp.ui.myGroups.joinGroup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.SearchGroupFirebase;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

public class JoinGroupViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Group>> result;

    public void search(String name){
        this.result = SearchGroupFirebase.searchGroups(name);
    }

    public MutableLiveData<ArrayList<Group>> getResult() {
        return result;
    }
}