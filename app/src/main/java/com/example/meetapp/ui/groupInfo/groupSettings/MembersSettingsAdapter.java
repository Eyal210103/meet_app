package com.example.meetapp.ui.groupInfo.groupSettings;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersSettingsAdapter extends RecyclerView.Adapter<MembersSettingsAdapter.MembersSettingsViewHolder> {

    private Context context;
    private  ArrayList<MutableLiveData<User>> members;
    private boolean isManager;

    public MembersSettingsAdapter(Context context, ArrayList<MutableLiveData<User>> members, boolean isManager) {
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

            holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    final int index = position;
                    menu.add(index,0,0, context.getString(R.string.menu_option_view_profile)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //Group group = groups.get(index).getValue();
                            return false;
                        }
                    });
                    if (isManager){
                        menu.add(index,1,1,context.getString(R.string.menu_option_set_manager)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //Group group = groups.get(index).getValue();
                                return false;
                            }
                        });
                        menu.add(index,2,2,context.getString(R.string.menu_option_remove_from_group)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //Group group = groups.get(index).getValue();
                                return false;
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
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
