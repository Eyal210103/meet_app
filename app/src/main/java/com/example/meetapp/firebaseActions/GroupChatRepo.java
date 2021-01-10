package com.example.meetapp.firebaseActions;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Group;
import com.example.meetapp.model.Message;
import com.example.meetapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatRepo{

    private final ArrayList<MutableLiveData<Message>> list = new ArrayList<>();
    private final HashMap<String, String> ids = new HashMap<>();
    private final HashMap<String, MutableLiveData<User>> userHashMap = new HashMap<>();
    private final String groupId;
    private Group group;


    public GroupChatRepo(String groupId) {
        this.groupId = groupId;
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                group = snapshot.getValue(Group.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public MutableLiveData<ArrayList<MutableLiveData<Message>>> getMessages() {
        final MutableLiveData<ArrayList<MutableLiveData<Message>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(list);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (!ids.containsKey(message.getId())) {
                    list.add(putMessageData(message.getId()));
                    mutableLiveData.setValue(list);
                    ids.put(message.getId(), message.getId());
                }
                if (!userHashMap.containsKey(message.getSenderId())) {
                    MutableLiveData<User> userMutableLiveData = putUserData(message.getSenderId());
                    userHashMap.put(message.getSenderId(), userMutableLiveData);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                for (MutableLiveData<Message> m : list) {
                    if (message.getId().equals(m.getValue().getId())) {
                        list.remove(m);
                        ids.remove(message);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.CHAT_CHILDES).addChildEventListener(childEventListener);
        return mutableLiveData;
    }

    private MutableLiveData<Message> putMessageData(String id) {
        MutableLiveData<Message> messageMLD = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.CHAT_CHILDES).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                messageMLD.setValue(message);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
        return messageMLD;
    }

    private MutableLiveData<User> putUserData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.USER_CHILDES).child(key);
        final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userMutableLiveData.setValue(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });
        return  userMutableLiveData;
    }

    public void sendMessage(Message message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.CHAT_CHILDES).push();
        message.setGroupName(group.getName());
        message.setId(reference.getKey());
        reference.setValue(message);
    }
    public void sendImageMessage(Message message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES)
                .child(this.groupId).child(FirebaseTags.CHAT_CHILDES).push();
        message.setGroupName(group.getName());
        message.setId(reference.getKey());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StorageUpload.uploadChatImage(null,GroupChatRepo.this.groupId,message.getId(), Uri.parse(message.getUrl()));
                reference.setValue(message);
            }
        });
        thread.start();
    }



}
