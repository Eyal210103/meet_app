package com.example.meetapp.ui.groupInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.meetapp.firebaseActions.GroupsMembersRepo;
import com.example.meetapp.ui.chat.GroupChatFragment;
import com.example.meetapp.ui.groupInfo.groupDashboard.GroupDashboardFragment;
import com.example.meetapp.ui.groupInfo.groupsMeetings.GroupMeetingsFragment;
import com.example.meetapp.ui.socialMenu.myGroups.MyGroupsFragment;

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
        Bundle bundle = new Bundle();
        bundle.putString("id",groupId);
        switch (position) {
            case 0:
                GroupDashboardFragment groupDashboardFragment =  new GroupDashboardFragment();
                groupDashboardFragment.setArguments(bundle);
                groupDashboardFragment.setParent(groupInfoFragment);
                return groupDashboardFragment;
            case 1:
                GroupChatFragment groupChatFragment = new GroupChatFragment();
                groupChatFragment.setArguments(bundle);
                return groupChatFragment;
            case 2:
                GroupMeetingsFragment groupMeetingsFragment= new GroupMeetingsFragment();
                groupMeetingsFragment.setArguments(bundle);
                return groupMeetingsFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

