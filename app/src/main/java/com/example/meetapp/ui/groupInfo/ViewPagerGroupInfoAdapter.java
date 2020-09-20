package com.example.meetapp.ui.groupInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.meetapp.ui.chat.GroupChatFragment;
import com.example.meetapp.ui.groupInfo.groupDashboard.GroupDashboardFragment;
import com.example.meetapp.ui.groupInfo.groupsMeetings.GroupMeetingsFragment;
import com.example.meetapp.ui.socialMenu.myGroups.MyGroupsFragment;
import com.example.meetapp.ui.socialMenu.myMeetings.MyMeetingsFragment;

class ViewPagerGroupInfoAdapter extends FragmentStateAdapter {

    String groupId;
    GroupInfoFragment groupInfoFragment;

    public ViewPagerGroupInfoAdapter(FragmentActivity fragmentActivity, GroupInfoFragment groupInfoFragment, String groupId) {
        super((FragmentActivity) fragmentActivity);
        this.groupId = groupId;
        this.groupInfoFragment = groupInfoFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                GroupDashboardFragment groupDashboardFragment =  new GroupDashboardFragment();
                groupDashboardFragment.setParent(groupInfoFragment);
                return groupDashboardFragment;
            case 1:
                GroupChatFragment groupChatFragment = new GroupChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",groupId);
                groupChatFragment.setArguments(bundle);
                return groupChatFragment;
            case 2:
                return new GroupMeetingsFragment();
        }
        return new MyGroupsFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

