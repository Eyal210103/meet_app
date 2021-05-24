package com.example.meetapp.chatPushNotification;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.meetapp.R;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseDatabaseListening {

    private static FirebaseDatabaseListening instance = null;

    private final FcmAPI apiService;
    private boolean first;

    public static FirebaseDatabaseListening getInstance() {
        if (instance == null){
            instance = new FirebaseDatabaseListening();
        }
        return instance;
    }

    private FirebaseDatabaseListening() {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(FcmAPI.class);
        first = true;
    }

    public void startService(){
        String id = CurrentUser.getInstance().getId();
        FirebaseDatabase.getInstance().getReference("Users/"+id+"/Groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String groupId = snapshot.getValue(String.class);
                notificationListener(groupId);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void notificationListener(String groupId){
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot ds:snapshot.getChildren()) {
                    if (i== snapshot.getChildrenCount()-1){
                        Message message = ds.getValue(Message.class);
                        if (!message.getSenderId().equals(CurrentUser.getInstance().getId())) {
                            if (!first)
                                sendNotification(CurrentUser.getInstance().getId(), message);
                        }
                        break;
                    }
                    i++;
                }
                first = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
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