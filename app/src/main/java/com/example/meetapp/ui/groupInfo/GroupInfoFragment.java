package com.example.meetapp.ui.groupInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoFragment extends Fragment {

    private GroupInfoViewModel mViewModel;
    private MembersAdapter membersAdapter;
    private ViewPager2 viewPager;

    CircleImageView groupImage;
    TextView groupName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupInfoViewModel.class);
        mViewModel.init(getArguments().getString("group"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_info_fragment, container, false);

        groupImage = view.findViewById(R.id.group_info_group_civ);
        groupName = view.findViewById(R.id.group_info_group_name);


        RecyclerView recyclerViewMembers = view.findViewById(R.id.group_info_recyclerView_members);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMembers.setLayoutManager(llm);
        recyclerViewMembers.setHasFixedSize(true);
        membersAdapter = new MembersAdapter(this, mViewModel.getMembersMutableLiveData().getValue());
        recyclerViewMembers.setAdapter(membersAdapter);
        
        viewPager = view.findViewById(R.id.viewPager_group);
        viewPager.setNestedScrollingEnabled(true);
        ViewPagerGroupInfoAdapter adapter = new ViewPagerGroupInfoAdapter(requireActivity(),this,mViewModel.getGroupId());
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false);

        String[] titles = {"Dashboard" , "Chat" , "Meetings"};
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_group);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }}).attach();

        mViewModel.getMembersMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<User>> mutableLiveData) {
                membersAdapter.notifyDataSetChanged();
                for (MutableLiveData<User> u : mViewModel.getMembersMutableLiveData().getValue()) {
                    if (!u.hasObservers()) {
                        u.observe(getViewLifecycleOwner(), new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                membersAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });

        mViewModel.getGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                updateUI(group);
            }
        });


        view.findViewById(R.id.group_setting_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("id",getArguments().getString("group"));
                navController.navigate(R.id.action_groupInfoFragment_to_groupSettingsFragment, bundle);
            }
        });
        return view;
    }

    public void updateUI(Group group){
        Glide.with(requireActivity()).load(group.getPhotoUrl()).into(groupImage);
        groupName.setText(group.getName());
    }
    public void swipeToChat(){
        viewPager.setCurrentItem(1, true);
    }
    public void swipeToMeetings(){
        viewPager.setCurrentItem(2, true);
    }
}