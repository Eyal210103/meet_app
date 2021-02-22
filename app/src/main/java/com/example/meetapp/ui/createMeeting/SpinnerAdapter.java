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
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Group;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpinnerAdapter extends ArrayAdapter<LiveData<Group>> {

    ArrayList<MutableLiveData<Group>> groups;
    LayoutInflater layoutInflater;
    int resource;
    Context context;

    public SpinnerAdapter(@NonNull Activity context, int resource, @NonNull ArrayList<MutableLiveData<Group>> objects) {
        super(context, resource);
        groups = objects;
        this.resource = resource;
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
