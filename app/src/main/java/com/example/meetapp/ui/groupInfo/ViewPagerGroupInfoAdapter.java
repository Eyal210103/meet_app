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

public class ViewPagerGroupInfoAdapter extends FragmentPagerAdapter {

    String groupId;

    public ViewPagerGroupInfoAdapter(@NonNull FragmentManager fm ,String groupId) {
        super(fm);
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
//            case 0:
//                return FirstFragment.newInstance(0, "Page # 1");
            default:
                Bundle bundle = new Bundle();
                bundle.putString("id", groupId);
                GroupChatFragment groupChatFragment = new GroupChatFragment();
                groupChatFragment.setArguments(bundle);
                return groupChatFragment;
//            case 2:
//                return SecondFragment.newInstance(2, "Page # 3");
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
