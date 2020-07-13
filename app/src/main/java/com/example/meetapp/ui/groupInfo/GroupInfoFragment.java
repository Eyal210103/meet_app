package com.example.meetapp.ui.groupInfo;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.dataLoadListener.DataUpdatedListener;
import com.example.meetapp.model.Group;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupInfoFragment extends Fragment implements DataUpdatedListener {

    private GroupInfoViewModel mViewModel;
    MembersAdapter membersAdapter;
    RecyclerView recyclerViewMembers;
    public static GroupInfoFragment newInstance() {
        return new GroupInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_info_fragment, container, false);
        Group group = (Group) (getArguments().getSerializable("group"));
        mViewModel.init(this, group);
        CircleImageView groupImage = view.findViewById(R.id.group_info_group_civ);
        TextView groupName = view.findViewById(R.id.group_info_group_name);

        assert group != null;
        Glide.with(requireActivity()).load(group.getPhotoUrl()).into(groupImage);
        groupName.setText(group.getName());

        recyclerViewMembers = view.findViewById(R.id.group_info_recyclerView_members);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMembers.setLayoutManager(llm);
        recyclerViewMembers.setHasFixedSize(true);
        membersAdapter= new MembersAdapter(this.requireActivity(), mViewModel.membersMutableLiveData.getValue());
        recyclerViewMembers.setAdapter(membersAdapter);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(this).get(GroupInfoViewModel.class);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDataUpdated() {
        try {

            membersAdapter.notifyDataSetChanged();
        }catch (Exception ignored){

        }
    }
}