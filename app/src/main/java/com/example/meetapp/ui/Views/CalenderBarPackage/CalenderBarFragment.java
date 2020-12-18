package com.example.meetapp.ui.Views.CalenderBarPackage;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;

import java.util.ArrayList;
import java.util.Date;

public class CalenderBarFragment extends Fragment {

    private CalenderBarViewModel mViewModel;
    private CalenderBarAdapter adapter;
    private ArrayList<Date> days;
    LinearLayoutManager llm;
    private int position;
    private RecyclerView recyclerView;
    private Date today;

    public static CalenderBarFragment newInstance() {
        return new CalenderBarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.calender_bar_fragment, container, false);
        days = new ArrayList<>();
        days.add(Calendar.getInstance().getTime());
        recyclerView = view.findViewById(R.id.group_meetings_calender_recycler);
        TextView monthTextView = view.findViewById(R.id.group_meetings_month_calender_textView);
        ImageView arrowPrevIV = view.findViewById(R.id.group_meetings_arrow_back_imageView);
        ImageView arrowNextIV = view.findViewById(R.id.group_meetings_arrow_forward_imageView);

        this.adapter = new CalenderBarAdapter(this, this.days, mViewModel.getMeetings().getValue(), mViewModel.getGroupMeetings().getValue(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        llm = new LinearLayoutManager(this.requireActivity());
        llm.setOrientation(RecyclerView.HORIZONTAL);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llm);
        add30Days();

        arrowNextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add30Days();
                recyclerView.smoothScrollToPosition(llm.findFirstVisibleItemPosition() + 30);
            }
        });

        mViewModel.getMeetings().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<Meeting>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<Meeting>> liveData) {
                adapter.notifyDataSetChanged();
            }
        });

        mViewModel.getGroupMeetings().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<GroupMeeting>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<GroupMeeting>> liveData) {
                adapter.notifyDataSetChanged();
            }
        });

        return  view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CalenderBarViewModel.class);
        mViewModel.init(ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class),null); // TODO implements changes
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
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (null != holder) {
                holder.itemView.setBackgroundResource(R.drawable.calender_item_background);
                holder = (RecyclerView.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                holder.itemView.setBackgroundResource(R.drawable.selected_calender_item_background);
            }
//            Log.d("BACKGROUNGISSUE", "setViewBackground: " + position + "  " + i);
//            llm.findViewByPosition(position).setBackgroundResource(R.drawable.calender_item_background);
            position = i;
//            llm.findViewByPosition(position).setBackgroundResource(R.drawable.selected_calender_item_background);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}