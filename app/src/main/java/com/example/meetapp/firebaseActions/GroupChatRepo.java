package com.example.meetapp.firebaseActions;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.Message;
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

public class GroupChatRepo{

    private ArrayList<MutableLiveData<Message>> list = new ArrayList<>();
    private HashMap<String, String> ids = new HashMap<>();
    private HashMap<String, MutableLiveData<User>> userHashMap = new HashMap<>();
    private ChildEventListener childEventListener;
    private String groupId;
    private Group group;

    public GroupChatRepo(String groupId) {
        this.groupId = groupId;
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).addListenerForSingleValueEvent(new ValueEventListener() {
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
        this.childEventListener = new ChildEventListener() {
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
                    userHashMap.put(message.getSenderId(),userMutableLiveData);
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
                Log.d("observer", "onCancelled: ERROR:GET ChatRepo" );
            }
        };
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").addChildEventListener(childEventListener);
        return mutableLiveData;
    }

    private MutableLiveData<Message> putMessageData(String id) {
        MutableLiveData<Message> messageMLD = new MutableLiveData<>();
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                Log.d("", "onDataChange: " + message);
                messageMLD.setValue(message);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return messageMLD;
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
                Log.d("observer", "onCancelled: ERROR:putUserData ChatRepo" );
            }
        });
        return  userMutableLiveData;
    }

    public void sendMessage(Message message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").push();
        message.setGroupName(group.getName());
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
    public void sendImageMessage(Message message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").push();
        message.setGroupName(group.getName());
        message.setId(reference.getKey());
        StorageUpload.uploadChatImage(null,this.groupId,message.getId(), Uri.parse(message.getUrl()));
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



}
