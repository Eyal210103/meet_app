package com.example.meetapp.ui.Views.CalenderBarPackage;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.ConstantValues;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalenderBarAdapter extends RecyclerView.Adapter<CalenderBarAdapter.CalenderBarViewHolder> {

    private CalenderBarFragment context;
    private ArrayList<Date> days;
    private HashMap<String, LiveData<Meeting>> meetings;

    private int selected;

    public CalenderBarAdapter(CalenderBarFragment context, ArrayList<Date> days, HashMap<String ,LiveData<Meeting>> meetings) {
        this.context = context;
        this.days = days;
        this.meetings = meetings;
    }

    @NonNull
    @Override
    public CalenderBarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.speical_calender_item, parent,false);
        return new CalenderBarViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CalenderBarViewHolder holder, int position) {

        Date date = days.get(position);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        holder.day.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        holder.date.setText(""+calendar.get(Calendar.DAY_OF_MONTH));
        holder.invisibleDots();

        String key = "" + calendar.get(java.util.Calendar.YEAR) +  calendar.get(java.util.Calendar.MONTH) +  calendar.get(java.util.Calendar.DAY_OF_MONTH);


        boolean isGood = false;
        boolean isRegular = false;
        boolean isGroup =false;

        if (!meetings.isEmpty()){

            if (meetings.containsKey(key)){
                isGood = true;
                if (meetings.get(key).getValue() instanceof GroupMeeting) {
                    isGroup = true;
                }else {
                    isRegular = true;
                }
            }
        }


        if (isGood){
            holder.visibleDots();
        }else {
            holder.invisibleDots();
        }

        boolean finalIsGroup = isGroup;
        boolean finalIsRegular = isRegular;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
           //     holder.itemView.setBackgroundResource(R.drawable.selected_calender_item_background);
                selected = position;
                context.setViewBackground(position);
                if (finalIsGroup){
                    context.onClickDate(key, ConstantValues.MEETING_TYPE_GROUP);
                }else if(finalIsRegular){
                    context.onClickDate(key,ConstantValues.MEETING_TYPE_PUBLIC);
                }else {
                    context.onClickDate("","None");
                }
                //context.onClickDate();
            }
        });

        if (position != selected) {
            holder.itemView.setBackgroundResource(R.drawable.calender_item_background);
        }

        if (position == days.size() - 1)
            context.add30Days();
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void setDays(ArrayList<Date> days){
        this.days = days;
        try {
            this.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setDotsColor(int mode ,CalenderBarViewHolder holder){
        switch (mode){
            case 1:
                holder.dot1.setBackgroundResource(R.drawable.blue_dot);
                holder.dot2.setBackgroundResource(R.drawable.blue_dot);
                holder.dot3.setBackgroundResource(R.drawable.blue_dot);
                return;
            case 2:
                holder.dot1.setBackgroundResource(R.drawable.red_dot);
                holder.dot2.setBackgroundResource(R.drawable.red_dot);
                holder.dot3.setBackgroundResource(R.drawable.red_dot);
                return;
            case 3:
        }
    }


    public String getDayOfWeek(int day){
        switch (day){
            case android.icu.util.Calendar.SUNDAY:
                return context.getString(R.string.days_sunday);

            case android.icu.util.Calendar.MONDAY:
                return context.getString(R.string.days_monday);

            case android.icu.util.Calendar.TUESDAY:
                return context.getString(R.string.days_tuesday);

            case android.icu.util.Calendar.WEDNESDAY:
                return context.getString(R.string.days_wednesday);

            case android.icu.util.Calendar.THURSDAY:
                return context.getString(R.string.days_thursday);

            case android.icu.util.Calendar.FRIDAY:
                return context.getString(R.string.days_friday);

            case android.icu.util.Calendar.SATURDAY:
                return context.getString(R.string.days_saturday);
            default:
                return "";
        }
    }


    static class CalenderBarViewHolder extends RecyclerView.ViewHolder{
        TextView day;
        TextView date;
        View dot1 ,dot2, dot3;
        LinearLayout linearLayout;
        public CalenderBarViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_day_of_month_calendar_item);
            day = itemView.findViewById(R.id.tv_day_calendar_item);
            linearLayout = itemView.findViewById(R.id.dots_calendar);
            dot1 = itemView.findViewById(R.id.dot1);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
        }

        private void visibleDots(){
            this.linearLayout.setVisibility(View.VISIBLE);
            this.dot1.setVisibility(View.VISIBLE);
            this.dot2.setVisibility(View.VISIBLE);
            this.dot3.setVisibility(View.VISIBLE);
        }
        private void invisibleDots(){
            this.dot1.setVisibility(View.INVISIBLE);
            this.dot2.setVisibility(View.INVISIBLE);
            this.dot3.setVisibility(View.INVISIBLE);
        }
    }
}
