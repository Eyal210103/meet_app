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
    private final HashMap<String, LiveData<Group>> groupMap = new HashMap<>();
    private final MutableLiveData<ArrayList<LiveData<Group>>> mutableLiveData = new MutableLiveData<>();

    static UserGroupsRepo instance = null;

    private UserGroupsRepo() {
    }

    /**
     * getInstance as part of singleton pattern
     * @return the only instance of the class
     */
    public static UserGroupsRepo getInstance() {
        if (instance == null) {
            instance = new UserGroupsRepo();
            instance.loadGroups();
        }
        return instance;
    }

    /**
     * a method that responsible to add database listening to get the user's group's
     */
    private void loadGroups() {
        mutableLiveData.setValue(groupList);

        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance().getId()).child(FirebaseTags.GROUPS_CHILDES)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String key = snapshot.getValue(String.class);
                        if (!groupMap.containsKey(key)) {
                            LiveData<Group> groupMutableLiveData = getGroupsData(key);
                            groupList.add(groupMutableLiveData);
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
                            if (g != null && key.equals(g.getValue().getId())) {
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

    /**
     * @return LiveData of ArrayList of LiveData of Group
     */
    public LiveData<ArrayList<LiveData<Group>>> getGroups() {
        return mutableLiveData;
    }

    /**
     * @param id represents the id of the group that we want to load
     * @return LiveData of the group
     */
    private LiveData<Group> getGroupsData(String id) {
        MutableLiveData<Group> mutableLiveData = new MutableLiveData<>();
        Query reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mutableLiveData.setValue(snapshot.getValue(Group.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return mutableLiveData;
    }

    public HashMap<String, LiveData<Group>> getHashMapGroups() {
        return groupMap;
    }

    /**
     * a method that responsible to delete the user from a group
     * @param id represents the id of the group that the user want to leave
     */
    public void leaveGroup(String id) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(CurrentUser.getInstance().getId()).child(FirebaseTags.GROUPS_CHILDES).child(id).removeValue();
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(id).child(FirebaseTags.MEMBERS_CHILDES).child(CurrentUser.getInstance().getId()).removeValue();
    }
}
