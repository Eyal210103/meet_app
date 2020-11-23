package com.example.meetapp.ui.socialMenu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.example.meetapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SocialMenuFragment extends Fragment {

    private SocialMenuViewModel mViewModel;

    private String[] titles = new String[]{"Groups", "Chat", "Meetings"};
    ViewPager2 viewPager;
    TabLayout tabLayout;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.social_menu_fragment, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(requireActivity());
        viewPager.setAdapter(adapter);
        //viewPager.setUserInputEnabled(false);
      //  viewPager.
        tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }
        ).attach();

        if (getArguments() != null && getArguments().getString("action")!= null && getArguments().getString("action").equals("meetings")){
            viewPager.setCurrentItem(2,true);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(SocialMenuViewModel.class);
    }
}

