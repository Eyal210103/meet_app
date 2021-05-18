package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.model.Group;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import javax.inject.Singleton;

@Singleton
public class SearchGroupFirebase {
    private static final MutableLiveData<ArrayList<Group>> groupMutableLiveData = new MutableLiveData<>();

    public static LiveData<ArrayList<Group>> searchGroups(String name){
        ArrayList<Group> groups = new ArrayList<>();
        groupMutableLiveData.setValue(groups);
        groupMutableLiveData.getValue().clear();
        Query query = FirebaseDatabase.getInstance().getReference(FirebaseTags.GROUPS_CHILDES)
                .orderByChild(FirebaseTags.SORT_GROUPS_NAME).startAt(name).endAt(name + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Group group = snapshot.getValue(Group.class);
                Boolean isPublic = snapshot.child("isPublic").getValue(Boolean.class);
                group.setIsPublic(isPublic);
                if (!UserGroupsRepo.getInstance().getHashMapGroups().containsKey(group.getId())) {
                    groups.add(group);
                    groupMutableLiveData.postValue(groups);
                }
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
        return groupMutableLiveData;
    }

}
