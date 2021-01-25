package com.example.meetapp.ui.groupInfo.groupsMeetings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.model.meetings.GroupMeeting;

import java.util.HashMap;

public class GroupMeetingsViewModel extends ViewModel {
    MutableLiveData<HashMap<String,LiveData<GroupMeeting>>> liveData = new MutableLiveData<>();

    public LiveData<HashMap<String,LiveData<GroupMeeting>>> getMeetings() {
        return liveData;
    }
}