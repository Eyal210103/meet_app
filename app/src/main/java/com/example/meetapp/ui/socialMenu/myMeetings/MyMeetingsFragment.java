package com.example.meetapp.ui.socialMenu.myMeetings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.ui.MainActivityViewModel;
import com.example.meetapp.ui.Views.CalenderBarPackage.CalenderBar;

public class MyMeetingsFragment extends Fragment {

    private MainActivityViewModel mViewModel;

    public static MyMeetingsFragment newInstance() {
        return new MyMeetingsFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_meetings_fragment, container, false);
        CalenderBar calenderBar = new CalenderBar(this, R.layout.speical_calender_item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        calenderBar.setMeetings(mViewModel.getMeetingsList(),mViewModel.getGroupsMeetingsList());

        calenderBar.setNextDaysButton(view.findViewById(R.id.group_meetings_arrow_forward_imageView));
        calenderBar.setPreviousDaysButton(view.findViewById(R.id.group_meetings_arrow_back_imageView));
        RecyclerView recyclerView = view.findViewById(R.id.group_meetings_calender_recycler);
        calenderBar.setRecyclerView(recyclerView);
        calenderBar.setMonthTextView(view.findViewById(R.id.group_meetings_month_calender_textView));

        view.findViewById(R.id.my_meetings_create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_socialMenuFragment_to_createMeetingFragment);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
    }

}