package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Group;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SingleGroupRepo {
    final MutableLiveData<Group> groupMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Group> getGroupData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(key);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMutableLiveData.setValue(snapshot.getValue(Group.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return  groupMutableLiveData;
    }
}
