package com.example.meetapp.firebaseActions;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Group;
import com.google.firebase.database.*;

import java.util.ArrayList;

import javax.inject.Singleton;

@Singleton
public class SearchGroupFirebase {
    private static MutableLiveData<ArrayList<MutableLiveData<Group>>> mutableLiveData = new MutableLiveData<>();

    public static MutableLiveData<ArrayList<MutableLiveData<Group>>> searchGroups(String name){
        ArrayList<MutableLiveData<Group>> groups = new ArrayList<>();
        mutableLiveData.setValue(groups);
        mutableLiveData.getValue().clear();
        Query query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("name").startAt(name).endAt(name + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MutableLiveData<Group> gld = new MutableLiveData<>();
                gld.setValue(snapshot.getValue(Group.class));
                Log.d("", "onChildAdded: " + snapshot.getValue(Group.class).toString());
                groups.add(gld);
                mutableLiveData.setValue(groups);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        return mutableLiveData;
    }
}
