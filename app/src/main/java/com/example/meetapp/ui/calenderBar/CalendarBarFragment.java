package com.example.meetapp.ui.calenderBar;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInCalender;
import com.example.meetapp.databinding.CalenderBarFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.groupInfo.GroupInfoViewModel;
import com.example.meetapp.ui.myMeetings.MyMeetingsFragment;
import com.example.meetapp.ui.myMeetings.MyMeetingsViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CalendarBarFragment extends Fragment implements MonthListener{

    private final ViewModel mViewModel;
    private CalendarBarAdapter adapter;
    private ArrayList<Date> days;
    private LinearLayoutManager llm;
    private int position;
    private RecyclerView recyclerView;
    private final Fragment parent;
    private String goTo;

    TextView monthTextView;

    public CalendarBarFragment(MyMeetingsViewModel mViewModel, Fragment parent) {
        this.mViewModel = mViewModel;
        this.parent= parent;
    }
    public CalendarBarFragment(GroupInfoViewModel mViewModel, Fragment parent) {
        this.mViewModel = mViewModel;
        this.parent= parent;
    }

    public CalendarBarFragment(MyMeetingsViewModel mViewModel, String goTo, MyMeetingsFragment parent) {
        this.mViewModel = mViewModel;
        this.parent= parent;
        this.goTo = goTo;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        CalenderBarFragmentBinding binding = CalenderBarFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        days = new ArrayList<>();
        days.add(Calendar.getInstance().getTime());
        recyclerView = binding.groupMeetingsCalenderRecycler;
        monthTextView = binding.groupMeetingsMonthCalenderTextView;
        ImageView arrowPrevIV = binding.groupMeetingsArrowBackImageView;
        ImageView arrowNextIV = binding.groupMeetingsArrowForwardImageView;


        add30Days();

        arrowNextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add30Days();
                recyclerView.smoothScrollToPosition(llm.findFirstVisibleItemPosition() + 30);
            }
        });

        arrowPrevIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llm.findFirstVisibleItemPosition()>30) {
                    recyclerView.smoothScrollToPosition(llm.findFirstVisibleItemPosition() - 30);
                }else {
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });



        if (mViewModel instanceof MyMeetingsViewModel) {
            this.adapter = new CalendarBarAdapter(this, this.days, ((MyMeetingsViewModel)mViewModel).getMeetings().getValue());
            ((MyMeetingsViewModel)mViewModel).getMeetings().observe(getViewLifecycleOwner(), new Observer<HashMap<String, ArrayList<LiveData<Meeting>>>>() {
                @Override
                public void onChanged(HashMap<String, ArrayList<LiveData<Meeting>>> stringArrayListHashMap) {
                    adapter.notifyDataSetChanged();
                }
            });
        }else {
            this.adapter = new CalendarBarAdapter(this, this.days, ((GroupInfoViewModel)mViewModel).getMeetings().getValue(), Const.BUNDLE_GROUP_ID);

            ((GroupInfoViewModel)mViewModel).getMeetings().observe(getViewLifecycleOwner(), new Observer<HashMap<String, ArrayList<LiveData<GroupMeeting>>>>() {
                @Override
                public void onChanged(HashMap<String, ArrayList<LiveData<GroupMeeting>>> stringArrayListHashMap) {
                    adapter.notifyDataSetChanged();
                }
            });
        }

        if (this.goTo != null){
            this.adapter.goTo(goTo);
        }
        llm = new LinearLayoutManager(this.requireActivity());
        llm.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llm);

        return  view;
    }

    public void add30Days(){
        for (int i = 0; i < 30; i++) {
            long next = (long) (this.days.get(days.size()-1).getTime() + 8.64e+7);
            Date date = new Date(next);
            days.add(date);
        }
        if (adapter!=null)
            adapter.setDays(this.days);
    }

    public void setViewBackground(int i) {
        try {
            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
            if (null != holder) {
                holder.itemView.setBackgroundResource(R.drawable.calender_item_background);
                holder = recyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    holder.itemView.setBackgroundResource(R.drawable.selected_calender_item_background);
                }

            }
            position = i;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClickDate(String index, String type, long millis , int i){
        ((OnClickInCalender) parent).onClickIInCalender(index,type,millis,i);
    }

    @Override
    public void OnDateChanged(int month) {
        monthTextView.setText(getMonth(month));
    }

    public String getMonth(int month){
        switch (month){
            case android.icu.util.Calendar.JANUARY:
                return getString(R.string.months_january);

            case android.icu.util.Calendar.FEBRUARY:
                return getString(R.string.months_february);

            case android.icu.util.Calendar.MARCH:
                return getString(R.string.months_march);

            case android.icu.util.Calendar.APRIL:
                return getString(R.string.months_april);

            case android.icu.util.Calendar.MAY:
                return getString(R.string.months_may);

            case android.icu.util.Calendar.JUNE:
                return getString(R.string.months_june);

            case android.icu.util.Calendar.JULY:
                return getString(R.string.months_july);

            case android.icu.util.Calendar.AUGUST:
                return getString(R.string.months_august);

            case android.icu.util.Calendar.SEPTEMBER:
                return getString(R.string.months_september);

            case android.icu.util.Calendar.OCTOBER:
                return getString(R.string.months_october);

            case android.icu.util.Calendar.NOVEMBER:
                return getString(R.string.months_november);

            case android.icu.util.Calendar.DECEMBER:
                return getString(R.string.months_december);

            default:
                return "";
        }
    }
}