package com.example.meetapp.firebaseActions;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.dataLoadListener.GroupsLoadListener;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserGroupsRepo {

    Map<String,MutableLiveData<Group>> map = new HashMap<>();
    static UserGroupsRepo instance = null;
    private static Context context;

    public static UserGroupsRepo getInstance(Context context){
        UserGroupsRepo.context = context;
        if (instance == null){
            instance = new UserGroupsRepo();
        }
        return instance;
    }

    public MutableLiveData<Map<String,MutableLiveData<Group>>> getGroups(){
        loadGroups();
        MutableLiveData<Map<String,MutableLiveData<Group>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(map);
        return mutableLiveData;
    }

    private void loadGroups() {
        Query reference = FirebaseDatabase.getInstance().getReference().child(CurrentUser.getCurrentUser().getId()).child("Groups");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getValue(String.class);
                if (!map.containsKey(key)) {
                    map.put(key, putGroupsData(key));
                    GroupsLoadListener listener= (GroupsLoadListener)context;
                    listener.onGroupsLoad();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                map.remove(snapshot.getKey());
                GroupsLoadListener listener= (GroupsLoadListener)context;
                listener.onGroupsLoad();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private MutableLiveData<Group> putGroupsData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(key);
        final MutableLiveData<Group> groupMutableLiveData = new MutableLiveData<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMutableLiveData.setValue(snapshot.getValue(Group.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return  groupMutableLiveData;
    }
}
