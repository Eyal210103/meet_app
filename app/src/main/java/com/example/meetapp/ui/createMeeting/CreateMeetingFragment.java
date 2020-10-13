package com.example.meetapp.ui.createMeeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;

public class CreateMeetingFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_create_meeting, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.create_meeting_recyclerView);
        recyclerView.setAdapter(new SubjectAdapter(requireActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),5));
        return view;
    }
}