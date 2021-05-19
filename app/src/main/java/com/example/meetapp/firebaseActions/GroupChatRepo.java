package com.example.meetapp.firebaseActions;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.R;
import com.example.meetapp.chatPushNotification.Client;
import com.example.meetapp.chatPushNotification.FcmAPI;
import com.example.meetapp.chatPushNotification.NotificationData;
import com.example.meetapp.chatPushNotification.Sender;
import com.example.meetapp.chatPushNotification.Token;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.Message;
import com.example.meetapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
//import retrofit2.Response;

public class GroupChatRepo {

    private final FcmAPI apiService;
    private final ArrayList<LiveData<Message>> list = new ArrayList<>();
    private final HashMap<String, String> ids = new HashMap<>();
    private final HashMap<String, LiveData<User>> userHashMap = new HashMap<>();
    final MutableLiveData<ArrayList<LiveData<Message>>> mutableLiveData = new MutableLiveData<>();
    private final String groupId;
    private Group group;


    public GroupChatRepo(String groupId) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(FcmAPI.class);
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
        loadMessages();
    }

    private void loadMessages() {
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
                    LiveData<User> userMutableLiveData = putUserData(message.getSenderId());
                    userHashMap.put(message.getSenderId(), userMutableLiveData);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Message message = snapshot.getValue(Message.class);
                for (LiveData<Message> m : list) {
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
        FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.CHAT_CHILDES)
                .addChildEventListener(childEventListener);
    }

    public LiveData<ArrayList<LiveData<Message>>> getMessages() {
        return mutableLiveData;
    }

    private LiveData<Message> putMessageData(String id) {
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

    private LiveData<User> putUserData(String key) {
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
        return userMutableLiveData;
    }

    public void sendMessage(Message message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES).child(this.groupId).child(FirebaseTags.CHAT_CHILDES).push();
        message.setGroupName(group.getName());
        message.setId(reference.getKey());
        reference.setValue(message);
        for (LiveData<User> user:userHashMap.values()) {
            User userDAO = user.getValue();
            sendNotification(userDAO.getId(),message);
        }
        //sendNotification(CurrentUser.getInstance().getId(), message);
    }

    public void sendImageMessage(Message message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES)
                .child(this.groupId).child(FirebaseTags.CHAT_CHILDES).push();
        message.setGroupName(group.getName());
        message.setId(reference.getKey());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StorageUpload.uploadChatImage(null, GroupChatRepo.this.groupId, message.getId(), Uri.parse(message.getUrl()));
                reference.setValue(message);
            }
        });
        thread.start();
    }

    public void sendImageMessage(Message message, Bitmap photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseTags.GROUPS_CHILDES)
                .child(this.groupId).child(FirebaseTags.CHAT_CHILDES).push();
        message.setGroupName(group.getName());
        message.setId(reference.getKey());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StorageUpload.uploadChatImage(null, GroupChatRepo.this.groupId, message.getId(), photo);
                reference.setValue(message);
            }
        });
        thread.start();
    }

    private void sendNotification(String receiver, final Message message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    NotificationData data = new NotificationData(FirebaseAuth.getInstance().getUid(), R.mipmap.ic_launcher,
                            message.getSenderDisplayName()+": "+message.getContext(), message.getGroupName(), receiver);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, retrofit2.Response<Integer> response) {
                                    Log.d("onResponse----", "onResponse: " + "________________________");
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {
                                    Log.d("onResponse----", "onResponse: " + call.toString() + "++++++++++++++++++++++++" + t.getMessage());
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
