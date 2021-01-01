package com.example.meetapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.AvailableMeetingsRepo;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<MutableLiveData<Meeting>>> meetings;
    private HashMap<String,Integer> publicHash;

    public HomeViewModel() {
        meetings = AvailableMeetingsRepo.getInstance().getPublicMeetings();
        publicHash = AvailableMeetingsRepo.getInstance().getPublicHash();
    }

    public LiveData<ArrayList<MutableLiveData<Meeting>>> getMeetings() {
        return meetings;
    }

    public HashMap<String, Integer> getPublicHash() {
        return publicHash;
    }
}