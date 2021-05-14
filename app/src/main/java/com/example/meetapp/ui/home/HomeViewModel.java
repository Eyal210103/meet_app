package com.example.meetapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.AvailableMeetingsRepo;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<MutableLiveData<Meeting>>> meetings;
    private final HashMap<String, Integer> publicHash;
    private final HashMap<String, ArrayList<LiveData<Meeting>>> meetingToMarker;
    private final HashMap<String, String> markerIdToMeeting;

    public HomeViewModel() {
        meetings = AvailableMeetingsRepo.getInstance().getPublicMeetings();
        publicHash = AvailableMeetingsRepo.getInstance().getPublicHash();
        meetingToMarker = new HashMap<>();
        markerIdToMeeting = new HashMap<>();
    }

    public LiveData<ArrayList<MutableLiveData<Meeting>>> getMeetings() {
        return meetings;
    }

    public HashMap<String, Integer> getPublicHash() {
        return publicHash;
    }


    //TODO check when location changed
    public void addMeetingToMarker(String id, LiveData<Meeting> meeting) {
        String meetingId = meeting.getValue().getId();
        if (meetingToMarker.containsKey(id)) {
            if (!markerIdToMeeting.containsKey(meetingId) ||(markerIdToMeeting.containsKey(meetingId) && !markerIdToMeeting.get(meetingId).equals(id))) {
                meetingToMarker.get(id).add(meeting);
            }
        } else {
            ArrayList<LiveData<Meeting>> temp = new ArrayList<>();
            temp.add(meeting);
            meetingToMarker.put(id, temp);
        }
        markerIdToMeeting.put(meetingId,id);
    }

    public ArrayList<LiveData<Meeting>> getListOfMeetings(String markerId){
        return meetingToMarker.get(markerId);
    }
}