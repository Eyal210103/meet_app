package com.example.meetapp.ui.groupInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.meetapp.ui.socialMenu.GroupsChats.GroupsChatsFragment;
import com.example.meetapp.ui.socialMenu.myGroups.MyGroupsFragment;
import com.example.meetapp.ui.socialMenu.myMeetings.MyMeetingsFragment;

public class ViewPagerGroupInfoAdapter extends FragmentStateAdapter {


    public ViewPagerGroupInfoAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerGroupInfoAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ViewPagerGroupInfoAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyGroupsFragment();
            case 1:
                return new GroupsChatsFragment();
            case 2:
                return new MyMeetingsFragment();
        }
        return new MyGroupsFragment();
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
