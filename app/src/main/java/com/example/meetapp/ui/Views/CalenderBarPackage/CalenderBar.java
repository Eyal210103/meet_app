package com.example.meetapp.ui.Views.CalenderBarPackage;

import android.icu.util.Calendar;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.meetings.Meeting;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalenderBar {
    private Date today;
    private Fragment context;
    private RecyclerView recyclerView;
    private CalenderBarAdapter adapter;
    private TextView monthTextView;
    private View nextDays ,previousDays;
    private int layout;
    private ArrayList<Date> days;
    private MutableLiveData<HashMap<String, MutableLiveData<Meeting>>> meetings;
    private View.OnClickListener onClickDate;
    private int position;
    LinearLayoutManager llm;

    public CalenderBar(Fragment context , int layout , View.OnClickListener onClickListener) {
        position = 0;
        this.context = context;
        this.layout = layout;
        today = Calendar.getInstance().getTime();
        days = new ArrayList<>();
        days.add(today);
        addNextMonth();
        //addPreviousMonth();
        meetings = new MutableLiveData<>();
        HashMap<String, MutableLiveData<Meeting>> hashMap = new HashMap<>();
        hashMap.put(String.valueOf((today.getDay())),null);
        meetings.setValue(hashMap);
        llm = new LinearLayoutManager(context.requireActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.onClickDate = onClickListener;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setLayoutManager(llm);
        this.adapter = new CalenderBarAdapter(this.context.requireActivity(),this.days,this.meetings.getValue(), layout, onClickDate, this);
        recyclerView.setAdapter(adapter);
        if (nextDays != null) {
            nextDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                }
            });
        }
        if (previousDays != null){
            previousDays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        }
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ((adapter.getItemCount() - ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition())< 5){
                    addNextMonth();
                }
//                if (((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition() < 5){
//                    //addPreviousMonth();
//                }
                monthTextView.setText(getMonth(days.get(((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()).getMonth()));
            }
        });
      //  setViewBackground(0);

    }

    public void setMeetings(MutableLiveData<HashMap<String, MutableLiveData<Meeting>>> meetings) {
        this.meetings = meetings;
        meetings.observe(context.getViewLifecycleOwner(), new Observer<HashMap<String, MutableLiveData<Meeting>>>() {
            @Override
            public void onChanged(HashMap<String, MutableLiveData<Meeting>> stringMutableLiveDataHashMap) {
                adapter.notifyDataSetChanged();
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void setMonthTextView(TextView monthTextView) {
        this.monthTextView = monthTextView;
        monthTextView.setText(getMonth(today.getMonth()));
    }

    public void setNextDaysButton(View nextDays) {
        this.nextDays = nextDays;
    }

    public void setPreviousDaysButton(View previousDays) {
        this.previousDays = previousDays;
    }

    public void setLayout(int layout) {
        this.layout = layout;
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
//        if (seekBar!= null)
//            seekBar.setMax(adapter.getItemCount());
    }

    public void addPreviousMonth(){
        for (int i = 0; i < 30; i++) {
            long next = (long) (this.days.get(0).getTime() - 8.64e+7);
            Date date = new Date(next);
            days.add(0,date);
        }
        if (adapter!=null)
            adapter.notifyDataSetChanged();
//        if (seekBar!= null) {
//            seekBar.setMax(adapter.getItemCount());
//            seekBar.setProgress(30);
//        }
    }

    public void setViewBackground(int i) {
        try {
            llm.findViewByPosition(position).setBackgroundResource(R.drawable.calender_item_background);
            position = i;
            llm.findViewByPosition(position).setBackgroundResource(R.drawable.selected_calender_item_background);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
