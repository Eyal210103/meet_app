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
    ArrayList<MutableLiveData<User>> membersAL = new ArrayList<>();
    HashMap<String,String> ids = new HashMap<>();
    MutableLiveData<ArrayList<MutableLiveData<User>>> mutableLiveData = new MutableLiveData<>();
    private String groupId;
    ArrayList<String> managers;
    ChildEventListener childEventListener;

    public GroupSettingsRepo (String groupId){
        this.groupId = groupId;
    }

    public MutableLiveData<ArrayList<MutableLiveData<User>>> getWaitingUsers(){
        ids.clear();
        membersAL.clear();
        mutableLiveData.setValue(membersAL);
        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getValue(String.class);
                boolean isThere = false;
                if (ids.containsKey(key)){
                    isThere= true;
                }else {
                    ids.put(key,key);
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
                        ids.remove(key);
                        break;
                    }
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("observer", "onCancelled: ERROR:GET GroupsMembersRepo" );
            }
        };
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Waiting").addChildEventListener(childEventListener);
        return mutableLiveData;
    }

    private MutableLiveData<User> putUserData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMutableLiveData.setValue(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("observer", "onCancelled: ERROR:putUserData GroupsMembersRepo" );
            }
        });
        return userMutableLiveData;
    }


    public LiveData<ArrayList<String>> getManagers(){
        managers= new ArrayList<>();
        MutableLiveData<ArrayList<String>> mutableLiveData = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("manager").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s:snapshot.getChildren()){
                    managers.add(s.getValue(String.class));
                }
                mutableLiveData.setValue(managers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return mutableLiveData;
    }

    public void removeUser(String id){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Waiting").child(id).removeValue();
        removeFromList(id);
    }

    public void approveUser(String id){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Members").child(id).setValue(id);
        FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Groups").child(groupId).setValue(groupId);
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Waiting").child(id).removeValue();
        removeFromList(id);
    }

    private void removeFromList(String id){
        ids.remove(id);
        for (MutableLiveData<User> user: membersAL) {
            if (user.getValue() != null && user.getValue().getId().equals(id)){
                membersAL.remove(user);
                mutableLiveData.postValue(membersAL);
            }
        }
    }

    private void OnDetach(){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Members").removeEventListener(childEventListener);
    }
}
