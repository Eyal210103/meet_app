package com.example.meetapp.ui.groupInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.meetapp.ui.chat.GroupChatFragment;
import com.example.meetapp.ui.socialMenu.GroupsChats.GroupsChatsFragment;
import com.example.meetapp.ui.socialMenu.myGroups.MyGroupsFragment;
import com.example.meetapp.ui.socialMenu.myMeetings.MyMeetingsFragment;

class ViewPagerGroupInfoAdapter extends FragmentStateAdapter {

    String groupId;

    public ViewPagerGroupInfoAdapter(FragmentActivity fragmentActivity, String groupId) {
        super((FragmentActivity) fragmentActivity);
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyGroupsFragment();
            case 1:
                GroupChatFragment groupChatFragment = new GroupChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",groupId);
                groupChatFragment.setArguments(bundle);
                return groupChatFragment;
            case 2:
                return new MyMeetingsFragment();
        }
        return new MyGroupsFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

