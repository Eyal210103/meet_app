package com.example.meetapp.ui.Views.CalenderBarPackage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalenderBarAdapter extends RecyclerView.Adapter<CalenderBarAdapter.CalenderBarViewHolder> {

    private Context context;
    private ArrayList<Date> days;
    private HashMap<String, MutableLiveData<Meeting>> meetings;
    private int layout;
    private View.OnClickListener onClickListener;

    public CalenderBarAdapter(Context context, ArrayList<Date> days, HashMap<String, MutableLiveData<Meeting>> meetings, int layout, View.OnClickListener onClickDate) {
        this.context = context;
        this.days = days;
        this.meetings = meetings;
        this.layout = layout;
        this.onClickListener = onClickDate;
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
        calendar.set(date.getYear(),date.getMonth(),date.getDate());
        holder.day.setText(getThreeLetterDay(calendar.get(Calendar.DAY_OF_WEEK)));
        holder.date.setText(""+calendar.get(Calendar.DAY_OF_MONTH));
        invisibleDots(holder);
        if (meetings.containsKey(String.valueOf(date.getDay())));
            visibleDots(holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                holder.itemView.setBackgroundResource(R.drawable.selected_calender_item_background);
                onClickListener.onClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    private void visibleDots(CalenderBarViewHolder holder){
        holder.dot1.setVisibility(View.VISIBLE);
        holder.dot2.setVisibility(View.VISIBLE);
        holder.dot3.setVisibility(View.VISIBLE);
    }
    private void invisibleDots(CalenderBarViewHolder holder){
        holder.dot1.setVisibility(View.INVISIBLE);
        holder.dot2.setVisibility(View.INVISIBLE);
        holder.dot3.setVisibility(View.INVISIBLE);
    }

    public String getThreeLetterDay(int day){
        switch (day){
            case Calendar.SUNDAY:
                return "SUN";

            case Calendar.MONDAY:
                return "MON";

            case Calendar.TUESDAY:
                return "TUE";

            case Calendar.WEDNESDAY:
                return "WED";

            case Calendar.THURSDAY:
                return "THU";

            case Calendar.FRIDAY:
                return "FRI";

            case Calendar.SATURDAY:
                return "SAT";

            default:
                return "";
        }
    }

    static class CalenderBarViewHolder extends RecyclerView.ViewHolder{
        TextView day;
        TextView date;
        View dot1 ,dot2, dot3;
        public CalenderBarViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_day_of_month_calendar_item);
            day = itemView.findViewById(R.id.tv_day_calendar_item);
            dot1 = itemView.findViewById(R.id.dot1);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
        }
    }
}
