package com.example.meetapp.ui.myMeetings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MyMeetingsViewModel extends ViewModel {
    private LiveData<HashMap<String, ArrayList<LiveData<Meeting>>>> meetings;
    private HashMap<String,String> idsOfGroupMeetingsToGroup = null;


    public void init(MainActivityViewModel mainActivityViewModel, String id) {
        if (mainActivityViewModel != null) {
            this.meetings = mainActivityViewModel.getMeetingsMap();
            this.idsOfGroupMeetingsToGroup = mainActivityViewModel.getIdsOfGroupMeetingsToGroup();
        }
    }

    public LiveData<HashMap<String, ArrayList<LiveData<Meeting>>>> getMeetings() {
        return meetings;
    }

    public HashMap<String, String> getIdsOfGroupMeetingsToGroup() {
        return idsOfGroupMeetingsToGroup;
    }
}