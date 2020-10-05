package com.example.meetapp.notifications;

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

public class MessageNotificationService {

    private APIService apiService;
    private boolean first;

    public MessageNotificationService() {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
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

    public void notificationListener(String groupId){
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

    private void sendNotification(final String id, final Message key) {
        DatabaseReference token = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = token.orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Log.d("sendNotification", "onResponse: "+token.getToken());
                    Data data = new Data(FirebaseAuth.getInstance().getCurrentUser().getUid(), R.mipmap.ic_launcher_round , key.getSenderDisplayName() + ": " + key.getContext(),key.getGroupName(),id);
                    Sender sender = new Sender(data,token.getToken());
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

}
