package com.example.meetapp.ui.home;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meetapp.R;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.meetings.MeetingsInfoDialog;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingsToLocationAdapter extends RecyclerView.Adapter<MeetingsToLocationAdapter.MeetingsToLocationViewHolder> {

    private final ArrayList<LiveData<Meeting>> meetings;
    private final FragmentActivity context;

    public MeetingsToLocationAdapter(FragmentActivity context, ArrayList<LiveData<Meeting>> meetings) {
        this.context = context;
        this.meetings = meetings;
    }

    @NonNull
    @Override
    public MeetingsToLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_meetings_in_location_adapter,parent,false);
        return new MeetingsToLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingsToLocationViewHolder holder, int position) {
        Meeting meeting = meetings.get(position).getValue();
        if (meeting != null){
            Glide.with(context).load(getSubjectIcon(meeting.getSubject())).into(holder.circleImageView);
            holder.location.setText(getAddress(new LatLng(meeting.getLatitude(),meeting.getLongitude())));
            holder.subject.setText(meeting.getSubject());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MeetingsInfoDialog meetingsInfoDialog = new MeetingsInfoDialog();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Const.BUNDLE_MEETING,meeting);
                    meetingsInfoDialog.setArguments(bundle);
                    meetingsInfoDialog.show(context.getSupportFragmentManager(),"Meeting Dialog");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return meetings.size();
    }
    public String getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude,location.longitude,1);
            try {
                Address obj = addresses.get(0);
                return obj.getAddressLine(0);
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error Has Occurred";
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


    static class MeetingsToLocationViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView subject, location;
        public MeetingsToLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.location_subject_textView);
            circleImageView = itemView.findViewById(R.id.home_location_subject_circleImageView);
            location = itemView.findViewById(R.id.location_textView);
        }
    }
}
