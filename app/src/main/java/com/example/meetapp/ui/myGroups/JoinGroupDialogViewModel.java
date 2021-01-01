package com.example.meetapp.ui.myGroups;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class JoinGroupDialogViewModel extends ViewModel {

    private LiveData<ArrayList<String>> imgUrl;

    public void init(String id){
        imgUrl = getImgUrls(id);
    }

    public LiveData<ArrayList<String>> getImgUrl() {
        return imgUrl;
    }

    private LiveData<ArrayList<String>> getImgUrls(String id){
        Query query = FirebaseDatabase.getInstance().getReference("Groups").child(id).child("Members").limitToFirst(3);
        MutableLiveData<ArrayList<String>> arrayListMutableLiveData = new MutableLiveData<>();
        ArrayList<String> strings = new ArrayList<>();
        arrayListMutableLiveData.setValue(strings);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()) {
                    FirebaseDatabase.getInstance().getReference("Users").child(ds.getKey()).child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            strings.add(snapshot.getValue(String.class));
                            arrayListMutableLiveData.postValue(strings);
                        }
                        @Override public void onCancelled(@NonNull DatabaseError error) { }});
                } } @Override public void onCancelled(@NonNull DatabaseError error) { }});
        return arrayListMutableLiveData;
    }

}
