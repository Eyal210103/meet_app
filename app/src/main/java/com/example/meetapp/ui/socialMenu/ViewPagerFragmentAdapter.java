package com.example.meetapp.ui.socialMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.meetapp.ui.socialMenu.GroupsChats.GroupsChatsFragment;
import com.example.meetapp.ui.socialMenu.myGroups.MyGroupsFragment;
import com.example.meetapp.ui.socialMenu.myMeetings.MyMeetingsFragment;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    public ViewPagerFragmentAdapter(FragmentActivity fragmentActivity) {
        super((FragmentActivity) fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new GroupsChatsFragment();
            case 1:
                return new MyGroupsFragment();
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
