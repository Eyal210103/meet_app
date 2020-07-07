package com.example.meetapp.ui.socialMenu.myGroups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    Context context;
    ArrayList<MutableLiveData<Group>> map;

    public GroupsAdapter(Context context, ArrayList<MutableLiveData<Group>> map) {
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
        Group current = map.get(position).getValue();
        holder.groupName.setText(current.getName());
        Glide.with(context).load(current.getPhotoUrl()).into(holder.groupImage);
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
