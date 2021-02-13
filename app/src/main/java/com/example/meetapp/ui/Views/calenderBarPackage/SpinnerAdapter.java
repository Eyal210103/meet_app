package com.example.meetapp.ui.Views.calenderBarPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.meetapp.R;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SpinnerAdapter extends ArrayAdapter<LiveData<Meeting>> {

    ArrayList<LiveData<Meeting>> meetings;
    LayoutInflater layoutInflater;
    int resource;
    Context context;

    public SpinnerAdapter(@NonNull Activity context, int resource, @NonNull ArrayList<LiveData<Meeting>> objects) {
        super(context, resource);
        meetings = objects;
        this.resource = resource;
        layoutInflater = context.getLayoutInflater();
        this.context = context;
    }

    @Override
    public int getCount() {
        return meetings.size();
    }

    @Nullable
    @Override
    public LiveData<Meeting> getItem(int position) {
        return meetings.get(position);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Meeting meeting = meetings.get(position).getValue();
        @SuppressLint("ViewHolder") View holder = layoutInflater.inflate(resource,parent,false);
        if (meeting!= null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(meeting.getMillis()));
            ((TextView)holder.findViewById(R.id.multi_meetings_hour_textView)).setText(String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
        // TODO   ((ImageView)holder.findViewById(R.id.multi_meetings_subject_imageView)).setImageResource();

        }
        return holder;
    }
    @SuppressLint("DefaultLocale")
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        Meeting meeting = meetings.get(position).getValue();
        @SuppressLint("ViewHolder") View holder = layoutInflater.inflate(resource,parent,false);
        if (meeting!= null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(meeting.getMillis()));
            ((TextView)holder.findViewById(R.id.multi_meetings_hour_textView)).setText(String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)));
           //TODO Glide.with(context).load(meeting.getPhotoUrl()).into((CircleImageView)holder.findViewById(R.id.select_group_adapter_civ));
        }
        return holder;
    }
}
