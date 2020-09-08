package com.example.meetapp.firebaseActions;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.message.Message;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatRepo {

    ArrayList<MutableLiveData<Message>> list = new ArrayList<MutableLiveData<Message>>();
    HashMap<String, String> ids = new HashMap<>();
    HashMap<String, User> userHashMap = new HashMap<>();
    private ChildEventListener childEventListener;
    private String groupId;


    public GroupChatRepo(String groupId) {
        this.groupId = groupId;
    }

    public MutableLiveData<ArrayList<MutableLiveData<Message>>> getMessages() {
        ids.clear();
        list.clear();

        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getValue(String.class);
                Log.d("observer" , "onChildAdded: " + key + "____________________________________________**********************************************************");
                boolean isThere = false;
                if (ids.containsKey(key)){
                    isThere= true;
                }else {
                    ids.put(key,key);
                }
                //TODO add users
//                if (!isThere) {
//                    MutableLiveData<User> groupMutableLiveData = putUserData(key);
//                    list.add(groupMutableLiveData);
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getValue(String.class);
                for (MutableLiveData<Message> u : list) {
                    if (key.equals(u.getValue().getId())) {
                        list.remove(u);
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
        MutableLiveData<ArrayList<MutableLiveData<Message>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(list);
        return mutableLiveData;
    }

    private MutableLiveData<User> putUserData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMutableLiveData.setValue(snapshot.getValue(User.class));
                Log.d("observer", "onChanged: " + snapshot.getValue(User.class).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("observer", "onCancelled: ERROR:putUserData GroupsMembersRepo" );

            }
        });
        return  userMutableLiveData;
    }
}
