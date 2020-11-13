package com.example.meetapp.ui.createMeeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.ui.MainActivityViewModel;

public class CreateMeetingFragment extends Fragment {

    MainActivityViewModel mainActivityViewModel;
    View view;
    Spinner SpinnerSelectGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_meeting, container, false);
        RecyclerView recyclerViewSubjects = view.findViewById(R.id.create_meeting_recyclerView);
        recyclerViewSubjects.setAdapter(new SubjectAdapter(requireActivity()));
        recyclerViewSubjects.setLayoutManager(new GridLayoutManager(requireActivity(), 5));

        SpinnerSelectGroup = view.findViewById(R.id.create_select_group_recyclerView);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(requireActivity(),R.layout.select_group_adapter,mainActivityViewModel.getGroups().getValue());

        SpinnerSelectGroup.setAdapter(spinnerAdapter);


        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_group){
                    ViewGroup.LayoutParams layoutParams = SpinnerSelectGroup.getLayoutParams();
                    layoutParams.height = 200;
                    SpinnerSelectGroup.setLayoutParams(layoutParams);

                }else if (checkedId == R.id.radio_meeting){
                    ViewGroup.LayoutParams layoutParams = SpinnerSelectGroup.getLayoutParams();
                    layoutParams.height = 0;
                    SpinnerSelectGroup.setLayoutParams(layoutParams);

                }
            }
        });
        return view;
    }

    public void displayList(){

    }
}