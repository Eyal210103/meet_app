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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
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
import com.example.meetapp.databinding.GroupSettingFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.meetapp.ui.groupInfo.GroupInfoFragment.getDominantColor;

public class GroupSettingsFragment extends Fragment implements OnClickInRecyclerView {

    private GroupSettingsViewModel mViewModel;
    private CircleImageView circleImageView;
    private TextView nameTextView;
    private TextView descriptionTextView;
    private LinearLayout linearLayoutWaiting;
    private LinearLayout themeLinearLayout;
    GroupSettingFragmentBinding binding;
    View view;
    boolean isThere = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupSettingsViewModel.class);
        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        String groupId = requireArguments().getString(Const.BUNDLE_GROUP_ID);
        mViewModel.init(mainActivityViewModel.getGroupsMap().get(groupId),getArguments().getString(Const.BUNDLE_GROUP_ID));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = GroupSettingFragmentBinding.inflate(inflater,container,false);
        view = binding.getRoot(); //inflater.inflate(R.layout.group_setting_fragment, container, false);

        this.circleImageView = binding.groupSettingsCircleImageView;//view.findViewById(R.id.group_settings_circleImageView);
        this.nameTextView = binding.groupSettingsNameTextView;//view.findViewById(R.id.group_settings_name_textView);
        this.descriptionTextView = binding.groupSettingsDescriptionTextView;
        this.linearLayoutWaiting = binding.groupSettingsLinearWaiting;//view.findViewById(R.id.group_settings_linear_waiting);
        this.themeLinearLayout = binding.groupSettingsGroupThemeLayout;//view.findViewById(R.id.group_settings_group_theme_layout);

        binding.groupSettingsEditImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditGroupDialog editGroupDialog = new EditGroupDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Const.BUNDLE_GROUP_ID,mViewModel.getGroup().getValue());
                editGroupDialog.setArguments(bundle);
                editGroupDialog.show(getChildFragmentManager(),"Edit Group");
            }
        });

        setInvisible();

        WaitingUsersAdapter adapter = new WaitingUsersAdapter(this, mViewModel.getPaddingUsers().getValue());
        RecyclerView recyclerView = binding.groupSettingsWaitingRecycler;
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        RecyclerView members = binding.groupSettingsMembers;//view.findViewById(R.id.group_settings_members);
        MembersSettingsAdapter settingsAdapter = new MembersSettingsAdapter(requireActivity(),mViewModel.getMembers().getValue(),isThere);
        members.setAdapter(settingsAdapter);
        LinearLayoutManager llm2 = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        members.setLayoutManager(llm2);


        mViewModel.getPaddingUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<User>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                if (mutableLiveData.isEmpty()){
                    setInvisible();
                }else {
                    for (MutableLiveData<User> u : mutableLiveData) {
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
            public void onChanged(ArrayList<String> strings) {
                for (String s:strings) {
                    if (s.equals(CurrentUser.getInstance().getId())){
                        isThere = true;
                        linearLayoutWaiting.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                if (!isThere){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(linearLayoutWaiting.getLayoutParams());
                    params.height= 0;
                    params.topMargin = 6;
                    linearLayoutWaiting.setLayoutParams(params);
                }
                adapter.notifyDataSetChanged();
            }
        });
        mViewModel.getMembers().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<User>> mutableLiveData) {
                settingsAdapter.notifyDataSetChanged();
                for (MutableLiveData<User> u : mutableLiveData) {
                    u.observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            settingsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onClickInRecyclerView(Object value, String action, int i) {
        if (action.equals(Const.ACTION_APPROVE)){
            mViewModel.approveUser((Integer)value);
            if (mViewModel.getPaddingUsers().getValue().size() == 0){
                setInvisible();
            }
        }else if(action.equals(Const.ACTION_REJECT)){
            mViewModel.rejectUser((Integer)value);
            if (mViewModel.getPaddingUsers().getValue().size() == 0){
                setInvisible();
            }
        }
    }

    private void setInvisible(){
        linearLayoutWaiting.setVisibility(View.INVISIBLE);
        binding.waitingReqTv.setVisibility(View.INVISIBLE);
    }
    private void setVisible(){
        linearLayoutWaiting.setVisibility(View.VISIBLE);
        binding.waitingReqTv.setVisibility(View.VISIBLE);
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
                themeLinearLayout.setBackground(gd);

                return false;
            }
        }).into(circleImageView);
        this.nameTextView.setText(group.getName());
        this.descriptionTextView.setText(group.getDescription());
    }
}