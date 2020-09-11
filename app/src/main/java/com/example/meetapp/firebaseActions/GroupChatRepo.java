package com.example.meetapp.firebaseActions;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.message.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatRepo {

    private ArrayList<Message> list = new ArrayList<Message>();
    private HashMap<String, String> ids = new HashMap<>();
    private HashMap<String, MutableLiveData<User>> userHashMap = new HashMap<>();
    private ChildEventListener childEventListener;
    private String groupId;

    
    public GroupChatRepo(String groupId) {
        this.groupId = groupId;
    }

    public MutableLiveData<ArrayList<Message>> getMessages() {
        ids.clear();
        list.clear();

        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message key = snapshot.getValue(Message.class);
                if (!ids.containsKey(key.getId())){
                    list.add(key);
                    ids.put(key.getId(), key.getId());
                }
                if (!userHashMap.containsKey(key.getSenderId())) {
                    MutableLiveData<User> userMutableLiveData = putUserData(key.getSenderId());
                    userHashMap.put(key.getSenderId(),userMutableLiveData);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message key = snapshot.getValue(Message.class);
                for (Message m : list) {
                    if (key.getId().equals(m.getId())) {
                        list.remove(m);
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
                Log.d("observer", "onCancelled: ERROR:GET ChatRepo" );
            }
        };
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").addChildEventListener(childEventListener);
        MutableLiveData<ArrayList<Message>> mutableLiveData = new MutableLiveData<>();
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
                Log.d("observer", "onCancelled: ERROR:putUserData ChatRepo" );

            }
        });
        return  userMutableLiveData;
    }

    public void sendMessage(Message message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").push();
        message.setId(reference.getKey());
        reference.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete())
                    Log.d("sendMessage", "onComplete: Succeed ChatRepo" );
                else
                    Log.d("sendMessage", "onComplete: ERROR ChatRepo" );

            }
        });
    }



    private void OnDetach(){
        //FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").removeEventListener(childEventListener);
    }
}
