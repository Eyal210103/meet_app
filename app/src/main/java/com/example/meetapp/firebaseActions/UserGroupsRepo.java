package com.example.meetapp.firebaseActions;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.meetapp.dataLoadListener.DataUpdatedListener;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

public class UserGroupsRepo {

    private static User thisUser = CurrentUser.getCurrentUser();
    ArrayList<MutableLiveData<Group>> list = new ArrayList<MutableLiveData<Group>>();
    HashMap<String,String> ids = new HashMap<>();

    static UserGroupsRepo instance = null;
    private static Context context;

    public static UserGroupsRepo getInstance(Context context){
        UserGroupsRepo.context = context;
        if (instance == null){
            instance = new UserGroupsRepo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<MutableLiveData<Group>>> getGroups(){
        ids.clear();
        list.clear();
        FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUser.getCurrentUser().getId()).child("Groups")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String key = snapshot.getValue(String.class);
                        Log.d("observer" , "onChildAdded: " + key + "____________________________________________**********************************************************");
                        boolean isThere = false;
                        if (ids.containsKey(key)){
                            isThere= true;
                        }else {
                            ids.put(key,key);
                        }
                        if (!isThere) {
                            MutableLiveData<Group> groupMutableLiveData = putGroupsData(key);
                            list.add(groupMutableLiveData);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        String key = snapshot.getValue(String.class);
                        for (MutableLiveData<Group> g : list) {
                            if (key.equals(g.getValue().getId())) {
                                list.remove(g);
                                ids.remove(key);
                                if (context != null) {
                                    DataUpdatedListener listener = (DataUpdatedListener) context;
                                    listener.onDataUpdated();
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        MutableLiveData<ArrayList<MutableLiveData<Group>>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(list);
        if (context != null) {
            DataUpdatedListener listener = (DataUpdatedListener) context;
            listener.onDataUpdated();
        }
        return mutableLiveData;
    }

    private MutableLiveData<Group> putGroupsData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(key);
        final MutableLiveData<Group> groupMutableLiveData = new MutableLiveData<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMutableLiveData.setValue(snapshot.getValue(Group.class));
                Log.d("observer", "onChanged: " + snapshot.getValue(Group.class).toString() + "**********************************************************");
                if (context != null) {
                    DataUpdatedListener listener = (DataUpdatedListener) context;
                    listener.onDataUpdated();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return  groupMutableLiveData;
    }

}
