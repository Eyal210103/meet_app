package com.example.meetapp.ui.groupInfo.groupSettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.User;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupSettingsFragment extends Fragment implements OnClickInRecyclerView {

    private GroupSettingsViewModel mViewModel;
    private CircleImageView circleImageView;
    private TextView nameTextView;
    private LinearLayout linearLayoutWaiting;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupSettingsViewModel.class);
        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        mViewModel.init(mainActivityViewModel.getGroupsMap().get(getArguments().getString("id")),getArguments().getString("id"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.group_setting_fragment, container, false);

        this.circleImageView = view.findViewById(R.id.group_settings_circleImageView);
        this.nameTextView = view.findViewById(R.id.group_settings_name_textView);
        this.linearLayoutWaiting = view.findViewById(R.id.group_settings_linear_waiting);

        setInvisible();

        WaitingUsersAdapter adapter = new WaitingUsersAdapter(this, mViewModel.getPaddingUsers().getValue());
        RecyclerView recyclerView = view.findViewById(R.id.group_settings_waiting_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        RecyclerView members = view.findViewById(R.id.group_settings_members);
        MembersSettingsAdapter settingsAdapter = new MembersSettingsAdapter(requireActivity(),mViewModel.getMembers().getValue());
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
                boolean isThere = false;
                for (String s:strings) {
                    if (s.equals(CurrentUser.getInstance().getId())){
                        isThere = true;
                        linearLayoutWaiting.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                if (!isThere){
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(linearLayoutWaiting.getLayoutParams());
                    params.height= 0;
                    params.topMargin = 6;
                    linearLayoutWaiting.setLayoutParams(params);
                }
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
    public void onClickInRecyclerView(Object value, String action) {
        if (action.equals("Approve")){
            mViewModel.approveUser((Integer)value);
            if (mViewModel.getPaddingUsers().getValue().size() == 0){
                setInvisible();
            }
        }else if(action.equals("Reject")){
            mViewModel.rejectUser((Integer)value);
            if (mViewModel.getPaddingUsers().getValue().size() == 0){
                setInvisible();
            }
        }
    }

    private void setInvisible(){
        linearLayoutWaiting.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.waiting_req_tv).setVisibility(View.INVISIBLE);
    }
    private void setVisible(){
        linearLayoutWaiting.setVisibility(View.VISIBLE);
        view.findViewById(R.id.waiting_req_tv).setVisibility(View.VISIBLE);
    }
    public void updateUI(Group group){
        Glide.with(requireActivity()).load(group.getPhotoUrl()).into(circleImageView);
        this.nameTextView.setText(group.getName());
    }
}