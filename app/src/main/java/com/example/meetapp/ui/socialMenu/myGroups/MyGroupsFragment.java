package com.example.meetapp.ui.socialMenu.myGroups;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetapp.R;
import com.example.meetapp.model.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGroupsFragment extends Fragment {

    private MyGroupsViewModel mViewModel;

    public static MyGroupsFragment newInstance() {
        return new MyGroupsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mViewModel = ViewModelProviders.of(this).get(MyGroupsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.my_groups_fragment, container, false);
        FloatingActionButton floatingActionButtonCreateGroup = view.findViewById(R.id.groups_create_group_fab);
        FloatingActionButton floatingActionButtonJoinGroup = view.findViewById(R.id.groups_join_group_fab);
        RecyclerView recyclerView = view.findViewById(R.id.groups_recycler_view);

        return view;
    }
}

class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    Context context;
    Map<String,MutableLiveData<Group>> map;

    public GroupsAdapter(Context context, Map<String,MutableLiveData<Group>> map) {
        this.context = context;
        this.map = map;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_adapter, parent, false);
        return new GroupsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class GroupsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView groupImage;
        TextView groupName;
        ImageView subject;
        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage =itemView.findViewById(R.id.groups_adapter_civ);
            groupName= itemView.findViewById(R.id.groups_adapter_group_name);
            subject = itemView.findViewById(R.id.groups_adapter_iv);
        }
    }
}