package com.example.meetapp.ui.socialMenu.myGroups;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    Fragment context;
    ArrayList<MutableLiveData<Group>> map;

    public GroupsAdapter(Fragment context, ArrayList<MutableLiveData<Group>> map) {
        this.context = context;
        this.map = map;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_adapter, parent, false);
        return new GroupsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        final Group current = map.get(position).getValue();
        if (current!= null) {
            holder.groupName.setText(current.getName());
            Glide.with(context.requireActivity()).load(current.getPhotoUrl()).into(holder.groupImage);
            Glide.with(context.requireActivity()).load(R.drawable.groups_subjects).into(holder.subject);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", current);
                    final NavController navController = Navigation.findNavController(context.requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_socialMenuFragment_to_groupInfoFragment, bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    static class GroupsViewHolder extends RecyclerView.ViewHolder {
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
