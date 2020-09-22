package com.example.meetapp.ui.Views.CalenderBarPackage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalenderBarAdapter extends RecyclerView.Adapter<CalenderBarAdapter.CalenderBarViewHolder> {

    private Context context;
    private ArrayList<Date> days;
    private HashMap<String, Meeting> meetings;

    public CalenderBarAdapter(Context context, ArrayList<Date> days, HashMap<String, Meeting> meetings) {
        this.context = context;
        this.days = days;
        this.meetings = meetings;
    }

    @NonNull
    @Override
    public CalenderBarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CalenderBarViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class CalenderBarViewHolder extends RecyclerView.ViewHolder{
        public CalenderBarViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
