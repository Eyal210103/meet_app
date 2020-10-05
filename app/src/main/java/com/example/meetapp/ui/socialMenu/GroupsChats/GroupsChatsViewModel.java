package com.example.meetapp.ui.socialMenu.GroupsChats;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.meetapp.firebaseActions.LastMessageRepo;
import com.example.meetapp.model.*;
import com.example.meetapp.model.Message;

import java.util.ArrayList;

public class GroupsChatsViewModel extends ViewModel {

    ArrayList<MutableLiveData<String>> users = new ArrayList<>();
    ArrayList<MutableLiveData<Message>> lastMessage = new ArrayList<>();
    ArrayList<MutableLiveData<Group>> groups;

    public void init(ArrayList<MutableLiveData<Group>> groups){
        this.groups = groups;
        for (int i = 0; i < groups.size(); i++) {
            LastMessageRepo lastMessageRepo = new LastMessageRepo(groups.get(i).getValue().getId());
            lastMessage.add(i,lastMessageRepo.getMessage());
            users.add(i,lastMessageRepo.getDisplayName());
        }
    }

    public ArrayList<MutableLiveData<String>> getUsers() {
        return users;
    }

    public ArrayList<MutableLiveData<Message>> getLastMessage() {
        return lastMessage;
    }

    public ArrayList<MutableLiveData<Group>> getGroups() {
        return groups;
    }
}