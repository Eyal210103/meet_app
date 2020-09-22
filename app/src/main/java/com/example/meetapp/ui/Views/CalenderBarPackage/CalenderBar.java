package com.example.meetapp.ui.Views.CalenderBarPackage;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


@RequiresApi(api = Build.VERSION_CODES.N)
public class CalenderBar {
    private Date today;
    private Context context;
    private RecyclerView recyclerView;
    private CalenderBarAdapter adapter;
    private TextView monthTextView;
    private Button nextDays;
    private Button previousDays;
    private int layout;
    private ArrayList<Date> days;
    private HashMap<String, Meeting> meetings;
    private View.OnClickListener onClickDate;


    public CalenderBar(Context context) {
        this.context = context;
        today = Calendar.getInstance().getTime();
        days = new ArrayList<>();
        addNextMonth();
        addPreviousMonth();
        meetings = new HashMap<>();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.adapter = new CalenderBarAdapter(this.context,this.days,this.meetings);
    }

    public void setMeetings(HashMap<String, Meeting> meetings) {
        this.meetings = meetings;
        adapter.notifyDataSetChanged();
    }

    public void setMonthTextView(TextView monthTextView) {
        this.monthTextView = monthTextView;
    }

    public void setNextDaysButton(Button nextDays) {
        this.nextDays = nextDays;
    }

    public void setPreviousDaysButton(Button previousDays) {
        this.previousDays = previousDays;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public void setOnClickDate(View.OnClickListener onClickDate) {
        this.onClickDate = onClickDate;
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

    public String getMonth(int month){
        switch (month){
            case Calendar.JANUARY:
                return "January";

            case Calendar.FEBRUARY:
                return "February";

            case Calendar.MARCH:
                return "March";

            case Calendar.APRIL:
                return "April";

            case Calendar.MAY:
                return "May";

            case Calendar.JUNE:
                return "June";

            case Calendar.JULY:
                return "July";

            case Calendar.AUGUST:
                return "August";

            case Calendar.SEPTEMBER:
                return "September";

            case Calendar.OCTOBER:
                return "October";

            case Calendar.NOVEMBER:
                return "November";

            case Calendar.DECEMBER:
                return "December";

            default:
                return "";

        }
    }

    public void addNextMonth(){
        for (int i = 0; i < 30; i++) {
            long next = (long) (this.days.get(days.size()-1).getTime() + 8.64e+7);
            Date date = new Date(next);
            days.add(date);
        }
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }

    public void addPreviousMonth(){
        for (int i = 0; i < 30; i++) {
            long next = (long) (this.days.get(0).getTime() - 8.64e+7);
            Date date = new Date(next);
            days.add(0,date);
        }
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }
}
