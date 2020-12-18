package com.example.meetapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.AvailableMeetingsRepo;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<MutableLiveData<Meeting>>> meetings;

    public HomeViewModel() {
        meetings = AvailableMeetingsRepo.getInstance().getPublicMeetings();
    }

    public LiveData<ArrayList<MutableLiveData<Meeting>>> getMeetings() {
        return meetings;
    }
}