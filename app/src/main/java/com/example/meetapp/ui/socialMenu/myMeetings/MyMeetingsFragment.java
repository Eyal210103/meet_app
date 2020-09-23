package com.example.meetapp.ui.socialMenu.myMeetings;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.meetapp.R;
import com.example.meetapp.ui.Views.CalenderBarPackage.CalenderBar;

public class MyMeetingsFragment extends Fragment {

    private MyMeetingsViewModel mViewModel;

    public static MyMeetingsFragment newInstance() {
        return new MyMeetingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_meetings_fragment, container, false);
        CalenderBar calenderBar = new CalenderBar(this,R.layout.speical_calender_item);
        calenderBar.setNextDaysButton(view.findViewById(R.id.group_meetings_arrow_forward_imageView));
        calenderBar.setPreviousDaysButton(view.findViewById(R.id.group_meetings_arrow_back_imageView));
        RecyclerView recyclerView = view.findViewById(R.id.group_meetings_calender_recycler);
        calenderBar.setRecyclerView(recyclerView);
        calenderBar.setSeekBar((SeekBar)view.findViewById(R.id.group_meetings_seekBar));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyMeetingsViewModel.class);
        // TODO: Use the ViewModel
    }

}