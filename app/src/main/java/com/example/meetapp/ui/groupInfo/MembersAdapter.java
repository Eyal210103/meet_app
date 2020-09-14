package com.example.meetapp.ui.groupInfo;

import android.content.Context;
import android.util.Log;
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

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder>{

    private Fragment context;
    private ArrayList<MutableLiveData<User>> members;

    public MembersAdapter(Fragment context, ArrayList<MutableLiveData<User>> members) {
        this.context = context;
        this.members = members;
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.members_adapter, parent, false);
        return new MembersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {
        User current = members.get(position).getValue();
        if (current != null){
            Glide.with(context.requireActivity()).load(current.getProfileImageUrl()).into(holder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MembersViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.members_adapter_civ);
        }
    }
}
