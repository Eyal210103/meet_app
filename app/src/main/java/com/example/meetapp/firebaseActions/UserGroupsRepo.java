package com.example.meetapp.firebaseActions;

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
    ArrayList<MutableLiveData<Group>> map = new ArrayList<MutableLiveData<Group>>();
    HashMap<String,String> ids = new HashMap<>();

    static UserGroupsRepo instance = null;
    private static Fragment context;

    public static UserGroupsRepo getInstance(Fragment context){
        UserGroupsRepo.context = context;
        if (instance == null){
            instance = new UserGroupsRepo();
        }
        return instance;
    }

    public MutableLiveData<ArrayList<MutableLiveData<Group>>> getGroups(){
        ids.clear();
        map.clear();
        loadGroups();
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
                            map.add(groupMutableLiveData);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        String key = snapshot.getValue(String.class);
                        for (MutableLiveData<Group> g : map) {
                            if (key.equals(g.getValue().getId())) {
                                map.remove(g);
                                ids.remove(key);
                                DataUpdatedListener listener= (DataUpdatedListener)context;
                                listener.onDataUpdated();
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
        mutableLiveData.setValue(map);
        DataUpdatedListener listener= (DataUpdatedListener)context;
        listener.onDataUpdated();
        return mutableLiveData;
    }

    private void loadGroups() {

    }

    private MutableLiveData<Group> putGroupsData(String key){
        Query reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(key);
        final MutableLiveData<Group> groupMutableLiveData = new MutableLiveData<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMutableLiveData.setValue(snapshot.getValue(Group.class));
                Log.d("observer", "onChanged: " + snapshot.getValue(Group.class).toString() + "**********************************************************");
                DataUpdatedListener listener= (DataUpdatedListener)context;
                listener.onDataUpdated();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return  groupMutableLiveData;
    }


//    public static MutableLiveData<ArrayList<MutableLiveData<Group>>> getGroups() {
//        final ArrayList<MutableLiveData<Group>> groupsObj = new ArrayList<>();
//        final GroupUpdatedListener listener= (GroupUpdatedListener)context;
//        final MutableLiveData<ArrayList<MutableLiveData<Group>>> g = new MutableLiveData<>();
//        final int before = 0;
//        FirebaseDatabase.getInstance().getReference().child("Users").child(thisUser.getId()).child("Groups").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                final ArrayList<String> groups = new ArrayList<String>();
//                try {
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        String group = postSnapshot.getValue(String.class);
//                        groups.add(group);
//                    }
//                    for (String id : groups) {
//                        FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("details").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (before > groups.size()){
//                                    groups.clear();
//                                    listener.onGroupUpdated();
//                                }
//                                Group group = dataSnapshot.getValue(Group.class);
//                                boolean isThere = false;
//                                int i = 0;
//                                for (MutableLiveData<Group> g : groupsObj) {
//                                    try {
//                                        if (g.getValue().getId().equals(group.getId())) {
//                                            MutableLiveData<Group> mlvg = new MutableLiveData<>();
//                                            mlvg.setValue(group);
//                                            groupsObj.set(i, mlvg);
//                                            listener.onGroupUpdated();
//                                            isThere = true;
//                                            break;
//                                        }
//                                        i++;
//                                    }catch (Exception ignored){}
//                                }
//                                if (!isThere) {
//                                    MutableLiveData<Group> mlvg = new MutableLiveData<>();
//                                    mlvg.setValue(group);
//                                    groupsObj.add(mlvg);
//                                }
//                                listener.onGroupUpdated();
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                        listener.onGroupUpdated();
//                    }
//                    g.setValue(groupsObj);
//                    listener.onGroupUpdated();
//                } catch (Exception ignored) {
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//
//        g.setValue(groupsObj);
//        return g;
//    }
}
