package com.example.meetapp.ui.socialMenu;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.meetapp.R;
import com.example.meetapp.ui.home.HomeFragment;
import com.example.meetapp.ui.socialMenu.GroupsChats.GroupsChatsFragment;
import com.example.meetapp.ui.socialMenu.myGroups.MyGroupsFragment;
import com.example.meetapp.ui.socialMenu.myMeetings.MyMeetingsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class SocialMenuFragment extends Fragment {

    private SocialMenuViewModel mViewModel;

    public static SocialMenuFragment newInstance() {
        return new SocialMenuFragment();
    }

    private String[] titles = new String[]{"Chat", "Groups", "Meetings"};
    ViewPager2 viewPager;
    TabLayout tabLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_menu_fragment, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(getActivity());
        viewPager.setAdapter(adapter);
        tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }
        ).attach();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SocialMenuViewModel.class);
    }
}

