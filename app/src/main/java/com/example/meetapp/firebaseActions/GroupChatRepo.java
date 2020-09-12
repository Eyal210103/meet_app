package com.example.meetapp.firebaseActions;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.R;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.model.message.Message;
import com.example.meetapp.notifications.APIService;
import com.example.meetapp.notifications.Client;
import com.example.meetapp.notifications.Data;
import com.example.meetapp.notifications.FirebaseMessaging;
import com.example.meetapp.notifications.MyResponse;
import com.example.meetapp.notifications.OreoNotification;
import com.example.meetapp.notifications.Sender;
import com.example.meetapp.notifications.Token;
import com.example.meetapp.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupChatRepo{

    private ArrayList<Message> list = new ArrayList<Message>();
    private HashMap<String, String> ids = new HashMap<>();
    private HashMap<String, MutableLiveData<User>> userHashMap = new HashMap<>();
    private ChildEventListener childEventListener;
    private String groupId;
    private APIService apiService;
    boolean notify = false;
    
    public GroupChatRepo(String groupId) {
        this.groupId = groupId;
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        FirebaseMessaging firebaseMessaging = new FirebaseMessaging();
    }

    public MutableLiveData<ArrayList<Message>> getMessages() {
        final MutableLiveData<ArrayList<Message>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(list);
        this.childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message key = snapshot.getValue(Message.class);
                if (!ids.containsKey(key.getId())) {
                    list.add(key);
                    mutableLiveData.setValue(list);
                    ids.put(key.getId(), key.getId());

                    if (!key.getSenderId().equals(CurrentUser.getCurrentUser().getId())) {
                        if (notify) {
                            sendNotification(CurrentUser.getCurrentUser().getId(), key);
                        }
                        notify = false;
                    }
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
        return mutableLiveData;
    }

    private void sendNotification(final String id, final Message key) {

        DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Log.d("sendNotification", "onResponse: "+token.getToken());
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.mipmap.ic_launcher_round , key.getSenderDisplayName() + ": " + key.getContext(),"New Message",id);
                    Sender sender = new Sender(data,token.getToken());
//                    Log.d("sendNotification", "onResponse:" + token.toString() + data.toString() + sender.toString());

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200){
                                assert response.body() != null;
                                if (response.body().success == 1){
                                    Log.d("sendNotification", "onResponse: failed");
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        notify = true;
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
        FirebaseDatabase.getInstance().getReference().child("Groups").child(this.groupId).child("Chat").removeEventListener(childEventListener);
    }

}
