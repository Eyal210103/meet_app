package com.example.meetapp.ui.groupInfo.groupDashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airbnb.lottie.L;
import com.example.meetapp.firebaseActions.LastMessageRepo;
import com.example.meetapp.model.message.Message;

public class GroupDashboardViewModel extends ViewModel {

    private MutableLiveData<Message> lastMessage;

    public void init(String groupId){
        LastMessageRepo lastMessageRepo =new LastMessageRepo(groupId);
        lastMessage = lastMessageRepo.getMessage();
    }

    public LiveData<Message> getLastMessage() {
        return lastMessage;
    }
}