package com.example.meetapp.firebaseActions;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupSettingsRepo {
    private final ArrayList<LiveData<User>> membersAL;
    private final ArrayList<String> managers;
    private final HashMap<String, String> idsHashMap;
    private final MutableLiveData<ArrayList<LiveData<User>>> waitingUsersListMutableLiveData;
    private final MutableLiveData<ArrayList<String>> managersIdsMutableLiveData;
    private final String groupId;

    public GroupSettingsRepo(String groupId) {
        this.groupId = groupId;
        membersAL = new ArrayList<>();
        idsHashMap = new HashMap<>();
        managers = new ArrayList<>();
        waitingUsersListMutableLiveData = new MutableLiveData<>();
        managersIdsMutableLiveData = new MutableLiveData<>();
        this.loadWaitingUsers();
        this.loadManagers();
    }

    private void loadWaitingUsers(){
        idsHashMap.clear();
        membersAL.clear();
        waitingUsersListMutableLiveData.setValue(membersAL);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getValue(String.class);
                boolean isThere = false;
                if (idsHashMap.containsKey(key)) {
                    isThere = true;
                } else {
                    idsHashMap.put(key, key);
                }
                if (!isThere) {
                    LiveData<User> userMutableLiveData = putUserData(key);
                    membersAL.add(userMutableLiveData);
                    waitingUsersListMutableLiveData.postValue(membersAL);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getValue(String.class);
                idsHashMap.remove(key);
                for (LiveData<User> u : membersAL) {
                    if (key.equals(u.getValue().getId())) {
                        membersAL.remove(u);
                        waitingUsersListMutableLiveData.postValue(membersAL);
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
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.WAITING_CHILDES).addChildEventListener(childEventListener);
    }

    public LiveData<ArrayList<LiveData<User>>> getWaitingUsers() {
        return waitingUsersListMutableLiveData;
    }

    private LiveData<User> putUserData(String key) {
        Query reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(key);
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMutableLiveData.setValue(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return userMutableLiveData;
    }

    private void loadManagers(){
        managersIdsMutableLiveData.setValue(managers);
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.MANAGER_CHILDES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                managers.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    managers.add(s.getValue(String.class));
                    Log.d("managers_____", "onChanged: " +s.getValue(String.class));
                }
                managersIdsMutableLiveData.postValue(managers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public LiveData<ArrayList<String>> getManagers() {
        return managersIdsMutableLiveData;
    }

    public void removeWaitingUser(String userId) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.WAITING_CHILDES).child(userId).removeValue();
        removeFromList(userId);
    }

    public void approveUser(String userId) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(groupId)
                .child(FirebaseTags.MEMBERS_CHILDES).child(userId).setValue(userId);
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(userId)
                .child(FirebaseTags.GROUPS_CHILDES).child(groupId).setValue(groupId);
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES)
                .child(groupId).child(FirebaseTags.WAITING_CHILDES).child(userId).removeValue();
        removeFromList(userId);
    }

    public void addManager(String userId){
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.MANAGER_CHILDES).child(userId).setValue(userId);
    }

    public void removeUser(String userId){
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.MEMBERS_CHILDES).child(userId).removeValue();
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(userId).child(FirebaseTags.GROUPS_CHILDES).child(groupId).removeValue();
    }

    private void removeFromList(String userId) {
        idsHashMap.remove(userId);
        for (LiveData<User> user : membersAL) {
            if (user.getValue() != null && user.getValue().getId().equals(userId)) {
                membersAL.remove(user);
                waitingUsersListMutableLiveData.postValue(membersAL);
            }
        }
    }
}
