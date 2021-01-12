package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class GroupsMembersRepo {
    private final ArrayList<MutableLiveData<User>> membersAL;
    private final HashMap<String,String> usersIdsMap;
    private final String groupId;
    MutableLiveData<ArrayList<MutableLiveData<User>>> mutableLiveData;
    ChildEventListener childEventListener;

    public GroupsMembersRepo (String groupId){
        this.groupId = groupId;
        membersAL = new ArrayList<MutableLiveData<User>>();
        usersIdsMap = new HashMap<>();
        mutableLiveData = new MutableLiveData<>();
        this.loadMembers();
    }

    private void loadMembers(){
        usersIdsMap.clear();
        membersAL.clear();
        mutableLiveData.setValue(membersAL);
        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getValue(String.class);
                boolean isThere = false;
                if (usersIdsMap.containsKey(key)){
                    isThere= true;
                }else {
                    usersIdsMap.put(key,key);
                }
                if (!isThere) {
                    MutableLiveData<User> userMutableLiveData = putUserData(key);
                    membersAL.add(userMutableLiveData);
                    mutableLiveData.setValue(membersAL);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getValue(String.class);
                for (MutableLiveData<User> u : membersAL) {
                    if (key.equals(u.getValue().getId())) {
                        membersAL.remove(u);
                        usersIdsMap.remove(key);
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
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.MEMBERS_CHILDES).addChildEventListener(childEventListener);
    }

    public MutableLiveData<ArrayList<MutableLiveData<User>>> getMembers(){
        return mutableLiveData;
    }

    private MutableLiveData<User> putUserData(String key){
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
        return  userMutableLiveData;
    }
}
