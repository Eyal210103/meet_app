package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Message;
import com.example.meetapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LastMessageRepo {

    private final String groupId;
    private MutableLiveData<String> displayName;
    private final MutableLiveData<Message> messageMutableLiveData ;
    private ChildEventListener childEventListener;

    public LastMessageRepo(String id) {
        this.groupId = id;
        messageMutableLiveData = new MutableLiveData<>();
        this.loadMessage();
    }

    private void loadMessage(){
        childEventListener = new ChildEventListener() {
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
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES)
                .child(this.groupId).child(FirebaseTags.CHAT_CHILDES).addChildEventListener(childEventListener);
    }

    public MutableLiveData<Message> getMessage(){
        return messageMutableLiveData;
    }

    public MutableLiveData<String> getDisplayName() {
        return displayName;
    }

    private  MutableLiveData<String> getUserDisplayName(String key){
        final MutableLiveData<String> userName = new MutableLiveData<>();
        Query reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(key);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(User.class).getDisplayName();
                userName.setValue(name);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return userName;
    }

    public void detachListener() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES)
                .child(this.groupId).child(FirebaseTags.CHAT_CHILDES).removeEventListener(childEventListener);
    }
}
