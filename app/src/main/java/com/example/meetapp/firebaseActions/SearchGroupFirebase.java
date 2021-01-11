package com.example.meetapp.firebaseActions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private static final MutableLiveData<ArrayList<MutableLiveData<Group>>> groupMutableLiveData = new MutableLiveData<>();

    public static MutableLiveData<ArrayList<MutableLiveData<Group>>> searchGroups(String name){
        ArrayList<MutableLiveData<Group>> groups = new ArrayList<>();
        groupMutableLiveData.setValue(groups);
        groupMutableLiveData.getValue().clear();
        Query query = FirebaseDatabase.getInstance().getReference(FirebaseTags.GROUPS_CHILDES).orderByChild("name").startAt(name).endAt(name + "\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MutableLiveData<Group> gld = new MutableLiveData<>();
                gld.setValue(snapshot.getValue(Group.class));
                if (!UserGroupsRepo.getInstance().groupMap.containsKey(gld.getValue().getId())) {
                    groups.add(gld);
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
