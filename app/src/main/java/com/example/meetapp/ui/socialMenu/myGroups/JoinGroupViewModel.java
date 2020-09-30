package com.example.meetapp.ui.socialMenu.myGroups;

import androidx.lifecycle.*;

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