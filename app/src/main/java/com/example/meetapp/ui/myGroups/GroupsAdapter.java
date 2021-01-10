package com.example.meetapp.ui.myGroups;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.ConstantValues;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {

    Fragment context;
    int type;
    ArrayList<MutableLiveData<Group>> groups;

    public GroupsAdapter(Fragment context, ArrayList<MutableLiveData<Group>> groups , int type) {
        this.context = context;
        this.groups = groups;
        this.type =type;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_adapter, parent, false);
        return new GroupsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        final Group current = groups.get(position).getValue();
        if (current != null) {
            holder.groupName.setText(current.getName());
            Glide.with(context.requireActivity()).load(current.getPhotoUrl()).into(holder.groupImage);
            Glide.with(context.requireActivity()).load(R.drawable.groups_subjects).into(holder.subject);
            Glide.with(context.getActivity()).load(getSubjectIcon(current.getSubject())).into(holder.subject);

            if (type == ConstantValues.TYPE_MYGROUPS) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantValues.BUNDLE_GROUP_ID, current.getId());
                        final NavController navController = Navigation.findNavController(context.requireActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_socialMenuFragment_to_groupInfoFragment, bundle);
                    }
                });
            }

            if (type == ConstantValues.TYPE_JOINGROUP) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(ConstantValues.BUNDLE_GROUP_ID, current);
                        final NavController navController = Navigation.findNavController(context.requireActivity(), R.id.nav_host_fragment);
                        navController.navigate(R.id.action_joinGroupFragment_to_joinGroupDialog, bundle);
                    }
                });
            }
            holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(position,0,0,ConstantValues.MENU_OPTION_LEAVE_GROUP).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            OnClickInRecyclerView onClickInRecyclerView = (OnClickInRecyclerView)context;
                            onClickInRecyclerView.onClickInRecyclerView(current.getId(),ConstantValues.ACTION_LEAVE);
                            return false;
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    private int getSubjectIcon(String subject){
        switch (subject){
            case ConstantValues.SUBJECT_RESTAURANT:
                return R.drawable.restaurant;
            case ConstantValues.SUBJECT_BASKETBALL:
                return R.drawable.basketball;
            case ConstantValues.SUBJECT_SOCCER:
                return R.drawable.soccer;
            case ConstantValues.SUBJECT_FOOTBALL:
                return R.drawable.football;
            case ConstantValues.SUBJECT_VIDEO_GAMES:
                return R.drawable.videogame;
            case ConstantValues.SUBJECT_MEETING:
                return R.drawable.meetingicon;
            default:
                return R.drawable.groupsicon;
        }
    }

    class GroupsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView groupImage;
        TextView groupName;
        ImageView subject;

        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.groups_adapter_civ);
            groupName = itemView.findViewById(R.id.groups_adapter_group_name);
            subject = itemView.findViewById(R.id.groups_adapter_iv);
        }
    }
}
