package com.example.meetapp.ui.groupInfo.groupSettings;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersSettingsAdapter extends RecyclerView.Adapter<MembersSettingsAdapter.MembersSettingsViewHolder> {

    private final Fragment context;
    private final ArrayList<LiveData<User>> members;
    private boolean isManager;

    public MembersSettingsAdapter(Fragment context, ArrayList<LiveData<User>> members, boolean isManager) {
        this.context = context;
        this.members = members;
        this.isManager = isManager;
    }

    @NonNull
    @Override
    public MembersSettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_members_adapter,parent,false);
        return new MembersSettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersSettingsViewHolder holder, int position) {
        User user = members.get(position).getValue();
        if (user!= null){
            Glide.with(context).load(user.getProfileImageUrl()).into(holder.circleImageView);
            holder.textView.setText(user.getDisplayName());

            if (!user.getId().equals(CurrentUser.getInstance().getId())) {
                holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        OnClickInRecyclerView onClickInRecyclerView = (OnClickInRecyclerView)context;
                        menu.add(position, 0, 0, context.getString(R.string.menu_option_view_profile)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return false;
                            }
                        });
                        if (isManager) {
                            menu.add(position, 1, 1, context.getString(R.string.menu_option_set_manager)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    onClickInRecyclerView.onClickInRecyclerView(user.getId(), Const.ACTION_SET_MANAGER,null);
                                    return false;
                                }
                            });
                            menu.add(position, 2, 2, context.getString(R.string.menu_option_remove_from_group)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    onClickInRecyclerView.onClickInRecyclerView(user.getId(), Const.ACTION_REMOVE,null);
                                    return false;
                                }
                            });
                        }
                    }
                });
            } else {
                holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.add(position, 0, 0, context.getString(R.string.menu_option_leave_group))
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                return false;
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setIsManager(boolean isManager){
        this.isManager = isManager;
        this.notifyDataSetChanged();
    }

    class MembersSettingsViewHolder extends RecyclerView.ViewHolder  {
        TextView textView;
        CircleImageView circleImageView;
        public MembersSettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.settings_members_circleImageView);
            textView = itemView.findViewById(R.id.settings_members_textView);
        }

    }
}
