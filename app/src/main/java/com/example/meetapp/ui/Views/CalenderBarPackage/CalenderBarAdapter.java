package com.example.meetapp.ui.Views.CalenderBarPackage;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalenderBarAdapter extends RecyclerView.Adapter<CalenderBarAdapter.CalenderBarViewHolder> {

    private CalenderBarFragment context;
    private ArrayList<Date> days;
    private ArrayList<LiveData<Meeting>> meetings;
    private HashMap<Integer, Integer> dateToIndexMHash ;
    private HashMap<Integer, Integer> dateToIndexGHash;
    private ArrayList<LiveData<GroupMeeting>> groupMeetings;
    private View.OnClickListener onClickListener;
    private int selected;
    private int indexM, indexG;

    public CalenderBarAdapter(CalenderBarFragment context, ArrayList<Date> days, ArrayList<LiveData<Meeting>> meetings, ArrayList<LiveData<GroupMeeting>> groupMeetings, View.OnClickListener onClickDate) {
        this.context = context;
        this.days = days;
        this.meetings = meetings;
        this.groupMeetings = groupMeetings;
        this.onClickListener = onClickDate;
        indexG = 0;
        indexM = 0;
        dateToIndexMHash = new HashMap<>();
        dateToIndexGHash = new HashMap<>();
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

        boolean isGood = false;
        boolean isRegular = false;
        boolean isGroup =false;
        int iM = indexM;
        int iG = indexG;

        Date date = days.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(),date.getMonth(),date.getDate());
        holder.day.setText(getThreeLetterDay(calendar.get(Calendar.DAY_OF_WEEK)));
        holder.date.setText(""+calendar.get(Calendar.DAY_OF_MONTH));
        invisibleDots(holder);

        if (!meetings.isEmpty() && meetings.size() > indexM) {
            Date mDate = new Date(meetings.get(indexM).getValue().getMillis());

            Log.d("", "onBindViewHolder: " + mDate.toString() + "  " + date.toString());
            Log.d("TAG", "onBindViewHolder: " + (date.getYear() == mDate.getYear() && date.getMonth() == mDate.getMonth() && date.getDay() == mDate.getDay()));

            if (date.compareTo(mDate) > 0) { //It returns a value greater than 0 if this Date is after the Date argument
                indexM++;
            }
            if (date.getYear() == mDate.getYear() && date.getMonth() == mDate.getMonth() && date.getDay() == mDate.getDay()) {
                isGood = true;
                isRegular = true;
                if (dateToIndexMHash.containsKey(position)){
                //    iM = dateToIndexMHash.get(date.getTime());
                }
                else{
                    dateToIndexMHash.put(position,indexM);
                    iM = indexM;
                }
            }
        }

        if (!groupMeetings.isEmpty() && groupMeetings.size() > indexG) {
            Date gDate = new Date(groupMeetings.get(indexG).getValue().getMillis());
            if (date.compareTo(gDate) > 0) {
                indexG++;
            }
            if (date.getYear() == gDate.getYear() && date.getMonth() == gDate.getMonth() && date.getDay() == gDate.getDay()) {
                isGood = true;
                isGroup = true;
                if (dateToIndexGHash.containsKey(date.getTime())){
                    iG = dateToIndexGHash.get(date.getTime());
                }else{
                    dateToIndexGHash.put(position,indexG);
                    iG = indexG;
                }
            }
        }

        if (dateToIndexGHash.containsKey(position)) {
            isGood = true;
            isGroup = true;
        }
        if (dateToIndexMHash.containsKey(position)) {
            isGood = true;
            isRegular = true;
        }

        Log.d("", "onBindViewHolder: " + isGood + "   " + position + "  " + date.toString() + dateToIndexGHash.toString() + dateToIndexGHash.containsKey(position) +  "   " + dateToIndexMHash.toString() + dateToIndexMHash.containsKey(position));

        if (isGood){
//            if (isRegular && isGroup){
//                setDotsColor(3,holder);
//            }else if (isRegular){
//                setDotsColor(2,holder);
//            }else if (isGroup){
//                setDotsColor(1,holder);
//            }
            visibleDots(holder);
        }else {
            invisibleDots(holder);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
           //     holder.itemView.setBackgroundResource(R.drawable.selected_calender_item_background);
                onClickListener.onClick(v);
                selected = position;
                context.setViewBackground(position);
            }
        });
        if (position != selected){
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
                return;
        }
    }

    private void visibleDots(CalenderBarViewHolder holder){
        holder.linearLayout.setVisibility(View.VISIBLE);
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
    }
}
