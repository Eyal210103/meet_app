package com.example.meetapp.firebaseActions;

import android.util.Log;

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
    ArrayList<MutableLiveData<User>> membersAL = new ArrayList<MutableLiveData<User>>();
    HashMap<String,String> ids = new HashMap<>();
    private String groupId;
    ChildEventListener childEventListener;

    public GroupsMembersRepo (String groupId){
        this.groupId = groupId;
    }

    public MutableLiveData<ArrayList<MutableLiveData<User>>> getMembers(){
        ids.clear();
        membersAL.clear();
        MutableLiveData<ArrayList<MutableLiveData<User>>> mutableLiveData = new MutableLiveData<>();
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
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Members").addChildEventListener(childEventListener);
        return mutableLiveData;
    }


    private MutableLiveData<User> putUserData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMutableLiveData.setValue(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("observer", "onCancelled: ERROR:putUserData GroupsMembersRepo" );
            }
        });
        return  userMutableLiveData;
    }

    private void OnDetach(){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Members").removeEventListener(childEventListener);
    }

}
