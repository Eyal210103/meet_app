package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserGroupsRepo {

    ArrayList<MutableLiveData<Group>> groupList = new ArrayList<MutableLiveData<Group>>();
    HashMap<String,MutableLiveData<Group>> groupMap = new HashMap<>();

    static UserGroupsRepo instance = null;

    public static UserGroupsRepo getInstance(){
        if (instance == null)
            instance = new UserGroupsRepo();
        return instance;
    }

    public MutableLiveData<ArrayList<MutableLiveData<Group>>> getGroups(){
        MutableLiveData<ArrayList<MutableLiveData<Group>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(groupList);
        if (groupList.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getInstance().getId()).child("Groups")
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            String key = snapshot.getValue(String.class);
                            if (!groupMap.containsKey(key)) {
                                MutableLiveData<Group> groupMutableLiveData = new MutableLiveData<>();
                                groupList.add(groupMutableLiveData);
                                putGroupsData(key, groupMutableLiveData);
                                mutableLiveData.postValue(groupList);
                                groupMap.put(key, groupMutableLiveData);
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            String key = snapshot.getValue(String.class);
                            for (MutableLiveData<Group> g : groupList) {
                                if (g!= null && key.equals(g.getValue().getId())) {
                                    groupList.remove(g);
                                    mutableLiveData.postValue(groupList);
                                    groupMap.remove(key);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
        return mutableLiveData;
    }

    private void putGroupsData(String key , MutableLiveData<Group> mutableLiveData){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mutableLiveData.setValue(snapshot.getValue(Group.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public HashMap<String, MutableLiveData<Group>> getHashMapGroups() {
        return groupMap;
    }

    public void leaveGroup(String id) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getInstance().getId()).child("Groups").child(id).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("Members").child(CurrentUser.getInstance().getId()).removeValue();
    }
}
