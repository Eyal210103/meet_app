package com.example.meetapp.firebaseActions;

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

public class GroupMembersRepo {
    private final ArrayList<LiveData<User>> membersAL;
    private final HashMap<String,String> usersIdsMap;
    private final String groupId;
    private final MutableLiveData<ArrayList<LiveData<User>>> mutableLiveData;
    private ChildEventListener childEventListener;

    public GroupMembersRepo(String groupId){
        this.groupId = groupId;
        membersAL = new ArrayList<LiveData<User>>();
        usersIdsMap = new HashMap<>();
        mutableLiveData = new MutableLiveData<>();
        this.loadMembers();
    }

    /**
     * a method that responsible to add database listening for group members
     */
    private void loadMembers(){
        mutableLiveData.setValue(membersAL);
        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getValue(String.class);
                if (!usersIdsMap.containsKey(key)) {
                    usersIdsMap.put(key,key);
                    membersAL.add(putUserData(key));
                    mutableLiveData.postValue(membersAL);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getValue(String.class);
                for (LiveData<User> u : membersAL) {
                    if (key != null && key.equals(u.getValue().getId())) {
                        membersAL.remove(u);
                        usersIdsMap.remove(key);
                        break;
                    }
                }
                mutableLiveData.postValue(membersAL);
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

    public LiveData<ArrayList<LiveData<User>>> getMembers(){
        return mutableLiveData;
    }

    /**
     *  a method that responsible to add database listening for a certain user who comes to a certain meeting
     * @param id  represents the user's UId
     * @return LiveData of the user
     */
    private LiveData<User> putUserData(String id){
        Query reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(id);
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMutableLiveData.postValue(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return  userMutableLiveData;
    }

    public void detachListener(){
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.MEMBERS_CHILDES).removeEventListener(childEventListener);
    }
}
