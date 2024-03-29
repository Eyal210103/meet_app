package com.example.meetapp.ui.groupInfo.groupSettings;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.databinding.GroupSettingsFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;

import static com.example.meetapp.ui.groupInfo.GroupInfoFragment.getDominantColor;

public class GroupSettingsFragment extends Fragment implements OnClickInRecyclerView {

    private GroupSettingsViewModel mViewModel;
    private LinearLayout linearLayoutWaiting;
    private LinearLayout themeLinearLayout;
    private GroupSettingsFragmentBinding binding;
    boolean isManagerAndIsPendingNotEmpty = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupSettingsViewModel.class);
        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        String groupId = requireArguments().getString(Const.BUNDLE_GROUP_ID);
        mViewModel.init(mainActivityViewModel.getGroupsMap().get(groupId), getArguments().getString(Const.BUNDLE_GROUP_ID));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = GroupSettingsFragmentBinding.inflate(inflater, container, false);

        this.linearLayoutWaiting = binding.groupSettingsLinearWaiting;
        this.themeLinearLayout = binding.groupSettingsGroupThemeLayout;

        binding.groupSettingsEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditGroupDialog editGroupDialog = new EditGroupDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Const.BUNDLE_GROUP_ID, mViewModel.getGroup().getValue());
                editGroupDialog.setArguments(bundle);
                editGroupDialog.show(getChildFragmentManager(), "Edit Group");
            }
        });

        setInvisible();

        WaitingUsersAdapter adapter = new WaitingUsersAdapter(this, mViewModel.getPendingUsers().getValue());
        RecyclerView recyclerView = binding.groupSettingsWaitingRecycler;
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        RecyclerView members = binding.groupSettingsMembers;//view.findViewById(R.id.group_settings_members);
        MembersSettingsAdapter settingsAdapter = new MembersSettingsAdapter(this, mViewModel.getMembers().getValue(),mViewModel.getManagers().getValue(),isManagerAndIsPendingNotEmpty);
        members.setAdapter(settingsAdapter);
        LinearLayoutManager llm2 = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        members.setLayoutManager(llm2);


        mViewModel.getPendingUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<User>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                if (mutableLiveData.isEmpty()) {
                    isManagerAndIsPendingNotEmpty = false;
                } else {
                    for (LiveData<User> u : mutableLiveData) {
                        u.observe(getViewLifecycleOwner(), new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                setVisible();
                                adapter.notifyDataSetChanged();
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

        mViewModel.getManagers().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> ids) {
                settingsAdapter.setManagers(ids);
                settingsAdapter.notifyDataSetChanged();
                boolean isThere = false;
                for (String s : ids) {
                    if (s.equals(CurrentUser.getInstance().getId())) {
                        settingsAdapter.setIsManager(true);
                        settingsAdapter.notifyDataSetChanged();
                        setVisible();
                        isThere = true;
                        break;
                    }
                }
                if (!isThere)
                    setInvisible();
                settingsAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.getMembers().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<User>> mutableLiveData) {
                settingsAdapter.notifyDataSetChanged();
                for (LiveData<User> u : mutableLiveData) {
                    u.observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            settingsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        return binding.getRoot();
    }

    private void setInvisible() {
        linearLayoutWaiting.setVisibility(View.INVISIBLE);
        binding.waitingReqTv.setVisibility(View.INVISIBLE);
    }

    private void setVisible() {
        //if (isManagerAndIsPendingNotEmpty) {
            linearLayoutWaiting.setVisibility(View.VISIBLE);
            binding.waitingReqTv.setVisibility(View.VISIBLE);
        //}
    }

    public void updateUI(Group group) {
        Glide.with(requireActivity()).load(group.getPhotoUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                int colorFromImg = getDominantColor(bitmap);
                int[] colors = {requireActivity().getColor(R.color.backgroundSec), colorFromImg, colorFromImg};

                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                gd.setCornerRadius(0f);
                themeLinearLayout.setBackground(gd);

                return false;
            }
        }).into(binding.groupSettingsCircleImageView);
        this.binding.groupSettingsNameTextView.setText(group.getName());
        this.binding.groupSettingsNameTextView.setSelected(true);
        this.binding.groupSettingsDescriptionTextView.setText(group.getDescription());
        this.binding.groupSettingsDescriptionTextView.setSelected(true);
    }

    @Override
    public void onClickInRecyclerView(Object value, String action, Integer i) {
        switch (action) {
            case Const.ACTION_APPROVE:
                mViewModel.approveUser((String) value);
                if (mViewModel.getPendingUsers().getValue().size() == 0) {
                    setInvisible();
                }
                break;
            case Const.ACTION_REJECT:
                mViewModel.rejectUser((String) value);
                if (mViewModel.getPendingUsers().getValue().size() == 0) {
                    setInvisible();
                }
                break;
            case Const.ACTION_SET_MANAGER: {
                String userId = (String) value;
                mViewModel.addManager(userId);
                break;
            }
            case Const.ACTION_REMOVE: {
                String userId = (String) value;
                mViewModel.removeUser(userId);
                break;
            }
        }
    }
}