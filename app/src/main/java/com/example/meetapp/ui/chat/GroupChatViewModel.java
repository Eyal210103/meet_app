package com.example.meetapp.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.GroupChatRepo;
import com.example.meetapp.model.Message;

import java.util.ArrayList;

public class GroupChatViewModel extends ViewModel {
    MutableLiveData<ArrayList<MutableLiveData<Message>>> messages = null;
    GroupChatRepo repo = null;

    public void init(String groupId) {
        if (repo == null) {
            repo = new GroupChatRepo(groupId);
            messages = repo.getMessages();
        }
    }
    public LiveData<ArrayList<MutableLiveData<Message>>> getMessages() { return messages;}

    public void sendMessage(Message message){
        repo.sendMessage(message);
    }
    public void sendImageMessage(Message message){
        repo.sendImageMessage(message);
    }

}