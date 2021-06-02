package com.example.meetapp.ui.createMeeting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpinnerGroupAdapter extends ArrayAdapter<LiveData<Group>> {

    ArrayList<LiveData<Group>> groups;
    LayoutInflater layoutInflater;
    int resource;
    Context context;

    public SpinnerGroupAdapter(@NonNull Activity context, @NonNull ArrayList<LiveData<Group>> objects) {
        super(context, R.layout.select_group_adapter);
        groups = objects;
        this.resource = R.layout.select_group_adapter;
        layoutInflater = context.getLayoutInflater();
        this.context = context;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Nullable
    @Override
    public LiveData<Group> getItem(int position) {
        return groups.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Group group = groups.get(position).getValue();
        @SuppressLint("ViewHolder") View holder = layoutInflater.inflate(resource,parent,false);
        holder.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (group!= null){
            ((TextView)holder.findViewById(R.id.select_group_adapter_group_name)).setText(group.getName());
            Glide.with(context).load(group.getPhotoUrl()).into((CircleImageView)holder.findViewById(R.id.select_group_adapter_civ));
        }
        return holder;
    }

    public int getIndex(String id){
        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getValue().getId().equals(id))
                return i;
        }
        return -1;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        Group group = groups.get(position).getValue();
        @SuppressLint("ViewHolder") View holder = layoutInflater.inflate(resource,parent,false);
        if (group!= null){
            ((TextView)holder.findViewById(R.id.select_group_adapter_group_name)).setText(group.getName());
            Glide.with(context).load(group.getPhotoUrl()).into((CircleImageView)holder.findViewById(R.id.select_group_adapter_civ));
        }
        return holder;
    }
}
