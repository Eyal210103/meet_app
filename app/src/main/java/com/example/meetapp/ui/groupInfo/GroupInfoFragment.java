package com.example.meetapp.ui.groupInfo;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.meetapp.R;
import com.example.meetapp.databinding.GroupInfoFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.ui.MainActivityViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class GroupInfoFragment extends Fragment {

    private GroupInfoViewModel mViewModel;
    private MembersAdapter membersAdapter;

    private GroupInfoFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupInfoViewModel.class);
        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        mViewModel.init(mainActivityViewModel.getGroupsMap().get(requireArguments().getString(Const.BUNDLE_GROUP_ID))
                ,requireArguments().getString(Const.BUNDLE_GROUP_ID));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = GroupInfoFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


        RecyclerView recyclerViewMembers = binding.groupInfoRecyclerViewMembers;
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMembers.setLayoutManager(llm);

        membersAdapter = new MembersAdapter(this, mViewModel.getMembersLiveData().getValue());
        recyclerViewMembers.setAdapter(membersAdapter);

        binding.viewPagerGroup.setNestedScrollingEnabled(true);
        ViewPagerGroupInfoAdapter adapter = new ViewPagerGroupInfoAdapter(this,mViewModel.getGroupId());
        binding.viewPagerGroup.setAdapter(adapter);
        binding.viewPagerGroup.setUserInputEnabled(false);

        String[] titles = {getResources().getString(R.string.title_dashboard), getResources().getString(R.string.chats), getResources().getString(R.string.meetings)};

        TabLayout tabLayout = binding.tabLayoutGroup;//view.findViewById(R.id.tab_layout_group);
        new TabLayoutMediator(tabLayout, binding.viewPagerGroup, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }}).attach();


        mViewModel.getMembersLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<User>> mutableLiveData) {
                for (LiveData<User> u : mutableLiveData) {
                    u.observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            membersAdapter.notifyDataSetChanged();
                        }
                    });
                }
                membersAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getGroup().observe(getViewLifecycleOwner(), new Observer<Group>() {
            @Override
            public void onChanged(Group group) {
                updateUI(group);
            }
        });


        binding.groupSettingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BUNDLE_GROUP_ID,getArguments().getString(Const.BUNDLE_GROUP_ID));
                navController.navigate(R.id.action_groupInfoFragment_to_groupSettingsFragment, bundle);
            }
        });

        return view;
    }

    public void updateUI(Group group){
        Glide.with(requireActivity()).load(group.getPhotoUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }
            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
                int colorFromImg = getDominantColor(bitmap);
                int[] colors = {requireActivity().getColor(R.color.backgroundSec),colorFromImg,colorFromImg};

                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                gd.setCornerRadius(0f);

                binding.groupInfoMain.setBackground(gd);
                return false;
            }
        }).into(binding.groupInfoGroupCiv);

        binding.groupInfoGroupName.setText(group.getName());
        binding.groupInfoGroupName.setSelected(true);
        binding.groupInfoSubjectImageView.setImageResource(getSubjectIcon(group.getSubject()));
    }

    public static int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 2, 2, true);
        final int color = newBitmap.getPixel( 1,1);
        newBitmap.recycle();
        return color;
    }

    public void swipeToChat(){
        binding.viewPagerGroup.setCurrentItem(1, true);
    }
    public void swipeToMeetings(){
        binding.viewPagerGroup.setCurrentItem(2, true);
    }

    private int getSubjectIcon(String subject){
        switch (subject){
            case Const.SUBJECT_RESTAURANT:
                return R.drawable.restaurant;
            case Const.SUBJECT_BASKETBALL:
                return R.drawable.basketball;
            case Const.SUBJECT_SOCCER:
                return R.drawable.soccer;
            case Const.SUBJECT_FOOTBALL:
                return R.drawable.football;
            case Const.SUBJECT_VIDEO_GAMES:
                return R.drawable.videogame;
            case Const.SUBJECT_MEETING:
                return R.drawable.meetingicon;
            default:
                return R.drawable.groupsicon;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.detach();
    }
}