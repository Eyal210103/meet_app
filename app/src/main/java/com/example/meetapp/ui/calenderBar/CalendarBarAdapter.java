package com.example.meetapp.ui.calenderBar;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalendarBarAdapter extends RecyclerView.Adapter<CalendarBarAdapter.CalenderBarViewHolder> {

    private final CalendarBarFragment context;
    private ArrayList<Date> days;
    private HashMap<String, ArrayList<LiveData<Meeting>>> publicMeetings;
    private HashMap<String, ArrayList<LiveData<GroupMeeting>>> groupMeetings;

    private String selectedDateString;
    private int selected;
    private int selectedIndex;
    private String goTo;


    public CalendarBarAdapter(CalendarBarFragment context, ArrayList<Date> days, HashMap<String, ArrayList<LiveData<Meeting>>> meetings) {
        this.context = context;
        this.days = days;
        this.publicMeetings = meetings;
    }

    public CalendarBarAdapter(CalendarBarFragment context, ArrayList<Date> days, HashMap<String, ArrayList<LiveData<GroupMeeting>>> meetings, String group) {
        this.context = context;
        this.days = days;
        this.groupMeetings = meetings;
    }

    @NonNull
    @Override
    public CalenderBarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.calender_item, parent, false);
        return new CalenderBarViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CalenderBarViewHolder holder, int position) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(days.get(position));
        holder.day.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        holder.date.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
        holder.invisibleDots();

        String key = Meeting.getDateString(calendar.getTime().getTime());//"" + calendar.get(java.util.Calendar.YEAR) + calendar.get(java.util.Calendar.MONTH) + calendar.get(java.util.Calendar.DAY_OF_MONTH);

        boolean isGood = false;
        boolean isRegular = false;
        boolean isGroup = false;

        if (key.equals(selectedDateString)) {
            context.setViewBackground(position);
        }

        int meetingCount = 0;

        if (publicMeetings != null && !publicMeetings.isEmpty()) {
            if (publicMeetings.containsKey(key)) {
                isGood = true;
                for (LiveData<Meeting> meeting : publicMeetings.get(key)) {
                    if (meeting.getValue() instanceof GroupMeeting) {
                        isGroup = true;
                    } else {
                        isRegular = true;
                    }
                    meetingCount++;
                }
            }
        }

        if (groupMeetings != null && !groupMeetings.isEmpty()) {
            if (groupMeetings.containsKey(key)) {
                isGood = true;
                isGroup = true;
                for (LiveData<GroupMeeting> meeting : groupMeetings.get(key)) {
                    meetingCount++;
                }
            }
        }

        if (isGood) {
            holder.visibleDots();
        } else {
            holder.invisibleDots();
        }

        boolean finalIsGroup = isGroup;
        boolean finalIsRegular = isRegular;
        int finalMeetingCount = meetingCount;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                selected = position;
                selectedDateString = key;
                if (finalMeetingCount <= 1) {
                    selectedIndex = 0;
                    setData(position, finalIsGroup, finalIsRegular, calendar.getTimeInMillis(), key);
                } else {
                    holder.spinner.performClick();
                    SpinnerAdapter spinnerAdapter;
                    if (publicMeetings != null) {
                        spinnerAdapter = new SpinnerAdapter(context.requireActivity(), R.layout.multi_meetings_adapter, publicMeetings.get(key));
                        holder.spinner.setAdapter(spinnerAdapter);
                    } else if (groupMeetings != null) {
                        spinnerAdapter = new SpinnerAdapter(context.requireActivity(), R.layout.multi_meetings_adapter, groupMeetings.get(key));
                        holder.spinner.setAdapter(spinnerAdapter);

                    }
                    holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int index, long id) {
                            selectedIndex = index;
                            setData(position, finalIsGroup, finalIsRegular, calendar.getTimeInMillis(), key);
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                //context.onClickDate();
            }
        });

        if (position != selected) {
            holder.itemView.setBackgroundResource(R.drawable.calender_item_background);
        } else {
            context.setViewBackground(position);
        }

        if (position == days.size() - 1)
            context.add30Days();

        sendMonth(calendar.get(Calendar.MONTH));

        if (goTo != null && goTo.equals(key)) {
            holder.itemView.performClick();
            goTo = null;
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void setDays(ArrayList<Date> days) {
        this.days = days;
        try {
            this.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goTo(String goTo){
        this.goTo = goTo;
    }

    private void sendMonth(int month) {
        ((MonthListener) context).OnDateChanged(month);
    }

    private void setData(int position, boolean finalIsGroup, boolean finalIsRegular, long millis, String key) {
        context.setViewBackground(position);
        if (finalIsGroup) {
            context.onClickDate(key, Const.MEETING_TYPE_GROUP, millis, selectedIndex);
        } else if (finalIsRegular) {
            context.onClickDate(key, Const.MEETING_TYPE_PUBLIC, millis, selectedIndex);
        } else {
            context.onClickDate("", "None", millis, 0);
        }
    }

    public String getDayOfWeek(int day) {
        switch (day) {
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

    static class CalenderBarViewHolder extends RecyclerView.ViewHolder {
        TextView day;
        TextView date;
        View dot1, dot2, dot3;
        LinearLayout linearLayout;
        Spinner spinner;

        public CalenderBarViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_day_of_month_calendar_item);
            day = itemView.findViewById(R.id.tv_day_calendar_item);
            linearLayout = itemView.findViewById(R.id.dots_calendar);
            dot1 = itemView.findViewById(R.id.multi_meeting_is_already_there_dot);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
            spinner = itemView.findViewById(R.id.spinner_calender_item);
        }

        private void visibleDots() {
            this.linearLayout.setVisibility(View.VISIBLE);
            this.dot1.setVisibility(View.VISIBLE);
            this.dot2.setVisibility(View.VISIBLE);
            this.dot3.setVisibility(View.VISIBLE);
        }

        private void invisibleDots() {
            this.dot1.setVisibility(View.INVISIBLE);
            this.dot2.setVisibility(View.INVISIBLE);
            this.dot3.setVisibility(View.INVISIBLE);
        }
    }
}
