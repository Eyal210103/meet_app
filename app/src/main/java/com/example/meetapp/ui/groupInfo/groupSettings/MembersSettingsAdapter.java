package com.example.meetapp.ui.groupInfo.groupSettings;

import android.content.Context;
import android.view.LayoutInflater;
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

    public MembersSettingsAdapter(Context context, ArrayList<MutableLiveData<User>> members) {
        this.context = context;
        this.members = members;
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
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    class MembersSettingsViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CircleImageView circleImageView;
        public MembersSettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.settings_members_circleImageView);
            textView = itemView.findViewById(R.id.settings_members_textView);
        }
    }
}
