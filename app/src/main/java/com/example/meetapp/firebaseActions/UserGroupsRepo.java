package com.example.meetapp.firebaseActions;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserGroupsRepo {

    ArrayList<Group> groups = new ArrayList<>();
    static UserGroupsRepo instance = null;

    public static UserGroupsRepo getInstance(Context context){
        if (instance == null){
            instance = new UserGroupsRepo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<Group>> getGroups(){
        loadGroups();
        MutableLiveData<ArrayList<Group>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(groups);
        return mutableLiveData;
    }

    private void loadGroups() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(CurrentUser.getCurrentUser().getId()).child("Groups");
        //TODO
    }
}
