package com.example.meetapp.firebaseActions;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.User;
import com.example.meetapp.model.message.Message;
import com.google.firebase.database.*;


public class LastMessageRepo {

    String id;
    private ChildEventListener childEventListener;
    MutableLiveData<String> displayName;

    public LastMessageRepo(String id) {
        this.id = id;
    }

    public MutableLiveData<Message> getMessage(){
        final MutableLiveData<Message> messageMutableLiveData = new MutableLiveData<>();
        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message key = snapshot.getValue(Message.class);
                messageMutableLiveData.setValue(key);
                displayName = getUserDisplayName(key.getSenderId());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.id).child("Chat").addChildEventListener(childEventListener);
        return messageMutableLiveData;
    }

    public MutableLiveData<String> getDisplayName() {
        return displayName;
    }

    private  MutableLiveData<String> getUserDisplayName(String key){
        final MutableLiveData<String> userName = new MutableLiveData<>();
        Query reference = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(User.class).getDisplayName();
                userName.setValue(name);
                Log.d("observer", "onChanged: " + snapshot.getValue(User.class).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("observer", "onCancelled: ERROR:putUserData ChatRepo" );

            }
        });
        return userName;
    }
}
