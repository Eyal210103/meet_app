package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
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

    private final ArrayList<LiveData<Group>> groupList = new ArrayList<LiveData<Group>>();
    private final HashMap<String,LiveData<Group>> groupMap = new HashMap<>();
    MutableLiveData<ArrayList<LiveData<Group>>> mutableLiveData = new MutableLiveData<>();

    static UserGroupsRepo instance = null;

    private UserGroupsRepo() {
    }

    public static UserGroupsRepo getInstance(){
        if (instance == null) {
            instance = new UserGroupsRepo();
            instance.loadGroups();
        }
        return instance;
    }

    private void loadGroups(){
        mutableLiveData.setValue(groupList);
        if (groupList.isEmpty()) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance().getId()).child(FirebaseTags.GROUPS_CHILDES)
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
                            for (LiveData<Group> g : groupList) {
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
    }

    public LiveData<ArrayList<LiveData<Group>>> getGroups(){
        return mutableLiveData;
    }

    private void putGroupsData(String key , MutableLiveData<Group> mutableLiveData){
        Query reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(key);
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

    public HashMap<String, LiveData<Group>> getHashMapGroups() {
        return groupMap;
    }

    public void leaveGroup(String id) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance().getId()).child(FirebaseTags.GROUPS_CHILDES).child(id).removeValue();
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(id).child(FirebaseTags.MEMBERS_CHILDES).child(CurrentUser.getInstance().getId()).removeValue();
    }
}
