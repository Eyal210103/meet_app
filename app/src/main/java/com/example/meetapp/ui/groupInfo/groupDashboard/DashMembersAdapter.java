package com.example.meetapp.ui.groupInfo.groupDashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashMembersAdapter extends RecyclerView.Adapter<DashMembersAdapter.DashMembersViewHolder> {

    private Fragment context;
    private ArrayList<MutableLiveData<User>> members;

    public DashMembersAdapter(Fragment context, ArrayList<MutableLiveData<User>> members) {
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public DashMembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_members_dash_adapter, parent, false);
        return new DashMembersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DashMembersViewHolder holder, int position) {
        Glide.with(context).load(members.get(position).getValue().getProfileImageUrl()).into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class DashMembersViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        public DashMembersViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.civ_members_dash);
        }
    }
}
