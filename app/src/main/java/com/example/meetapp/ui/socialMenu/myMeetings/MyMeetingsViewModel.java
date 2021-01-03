package com.example.meetapp.ui.socialMenu.myMeetings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;

public class MyMeetingsViewModel extends ViewModel {
    private LiveData<ArrayList<LiveData<Meeting>>> meetings;
    private LiveData<ArrayList<LiveData<GroupMeeting>>> groupMeetings;

    public void init(MainActivityViewModel mainActivityViewModel , String id) {
        if (mainActivityViewModel != null){
            this.groupMeetings = mainActivityViewModel.getGroupsMeetingsList();
            this.meetings = mainActivityViewModel.getMeetingsList();
        }
    }

    public LiveData<ArrayList<LiveData<Meeting>>> getMeetings() {
        return meetings;
    }

    public LiveData<ArrayList<LiveData<GroupMeeting>>> getGroupMeetings() {
        return groupMeetings;
    }
}