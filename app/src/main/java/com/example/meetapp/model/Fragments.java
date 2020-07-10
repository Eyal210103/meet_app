package com.example.meetapp.model;

import com.example.meetapp.ui.socialMenu.GroupsChats.GroupsChatsFragment;
import com.example.meetapp.ui.socialMenu.myGroups.MyGroupsFragment;
import com.example.meetapp.ui.socialMenu.myMeetings.MyMeetingsFragment;

public class Fragments {
    public static MyGroupsFragment myGroupsFragment = null;
    public static GroupsChatsFragment groupsChatsFragment = null;
    public static MyMeetingsFragment myMeetingsFragment = null;

    public static void reset() {
        if (myGroupsFragment == null || groupsChatsFragment == null || myMeetingsFragment == null) {
            myGroupsFragment = new MyGroupsFragment();
            groupsChatsFragment = new GroupsChatsFragment();
            myMeetingsFragment = new MyMeetingsFragment();
        }
    }
}
