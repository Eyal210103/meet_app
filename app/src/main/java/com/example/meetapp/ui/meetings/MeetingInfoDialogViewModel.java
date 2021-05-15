package com.example.meetapp.ui.meetings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.AvailableMeetingsRepo;
import com.example.meetapp.model.User;

import java.util.ArrayList;

public class MeetingInfoDialogViewModel extends ViewModel {

    LiveData<ArrayList<User>> users;

    public void init(String id){
        users = AvailableMeetingsRepo.getWhoComing(id);
    }

    public LiveData<ArrayList<User>> getUsers() {
        return users;
    }
}
