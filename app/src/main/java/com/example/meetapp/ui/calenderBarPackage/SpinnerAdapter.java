package com.example.meetapp.ui.calenderBarPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;

import com.example.meetapp.R;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SpinnerAdapter extends ArrayAdapter<LiveData<Meeting>> {

    ArrayList<LiveData<Meeting>> meetings;
    ArrayList<LiveData<GroupMeeting>> groupMeetings;

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

    public SpinnerAdapter(FragmentActivity context, int resource, ArrayList<LiveData<GroupMeeting>> liveData) {
        super(context, resource);
        groupMeetings = liveData;
        this.resource = resource;
        layoutInflater = context.getLayoutInflater();
        this.context = context;
    }

    @Override
    public int getCount() {
        if (meetings != null)
            return meetings.size();
        return groupMeetings.size();
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
        Meeting meeting;
        if (meetings != null)
            meeting = meetings.get(position).getValue();
        else
            meeting = groupMeetings.get(position).getValue();
        @SuppressLint("ViewHolder") View holder = layoutInflater.inflate(resource, parent, false);
        holder.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (meeting != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(meeting.getMillis()));
            ((TextView) holder.findViewById(R.id.multi_meetings_hour_textView)).setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            ((ImageView)holder.findViewById(R.id.multi_meetings_subject_imageView)).setImageResource(getSubjectIcon(meeting.getSubject()));
        }
        return holder;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Meeting meeting;
        if (meetings != null)
            meeting = meetings.get(position).getValue();
        else
            meeting = groupMeetings.get(position).getValue();
        @SuppressLint("ViewHolder") View holder = layoutInflater.inflate(resource, parent, false);
        if (meeting != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(meeting.getMillis()));
            ((TextView) holder.findViewById(R.id.multi_meetings_hour_textView)).setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            ((ImageView)holder.findViewById(R.id.multi_meetings_subject_imageView)).setImageResource(getSubjectIcon(meeting.getSubject()));
        }
        return holder;
    }
    private int getSubjectIcon(String subject){
        switch (subject){
            case Const.SUBJECT_RESTAURANT:
                return R.drawable.restaurant;
            case Const.SUBJECT_BASKETBALL:
                return R.drawable.basketball;
            case Const.SUBJECT_SOCCER:
                return R.drawable.soccer;
            case Const.SUBJECT_FOOTBALL:
                return R.drawable.football;
            case Const.SUBJECT_VIDEO_GAMES:
                return R.drawable.videogame;
            case Const.SUBJECT_MEETING:
                return R.drawable.meetingicon;
            default:
                return R.drawable.groupsicon;
        }
    }
}
