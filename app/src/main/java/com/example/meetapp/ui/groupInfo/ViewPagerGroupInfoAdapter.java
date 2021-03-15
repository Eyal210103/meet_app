package com.example.meetapp.ui.groupInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.meetapp.model.Consts;
import com.example.meetapp.ui.chat.GroupChatFragment;
import com.example.meetapp.ui.groupInfo.groupDashboard.GroupDashboardFragment;
import com.example.meetapp.ui.groupInfo.groupsMeetings.GroupMeetingsFragment;

class ViewPagerGroupInfoAdapter extends FragmentStateAdapter {

    String groupId;
    GroupInfoFragment groupInfoFragment;
    private final GroupDashboardFragment groupDashboardFragment;
    private final GroupChatFragment groupChatFragment;
    private final GroupMeetingsFragment groupMeetingsFragment;

    public ViewPagerGroupInfoAdapter(GroupInfoFragment groupInfoFragment, String groupId) {
        super(groupInfoFragment.requireActivity());
        this.groupId = groupId;
        this.groupInfoFragment = groupInfoFragment;

        Bundle bundle = new Bundle();
        bundle.putString(Consts.BUNDLE_GROUP_ID,groupId);
        groupDashboardFragment =  new GroupDashboardFragment();
        groupDashboardFragment.setArguments(bundle);
        groupDashboardFragment.setParent(groupInfoFragment);

        groupChatFragment = new GroupChatFragment();
        groupChatFragment.setArguments(bundle);

        groupMeetingsFragment= new GroupMeetingsFragment();
        groupMeetingsFragment.setArguments(bundle);
        groupMeetingsFragment.setParent(groupInfoFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(Consts.BUNDLE_GROUP_ID,groupId);
        switch (position) {
            case 0:
                return groupDashboardFragment;
            case 1:
                return groupChatFragment;
            case 2:
                return groupMeetingsFragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

