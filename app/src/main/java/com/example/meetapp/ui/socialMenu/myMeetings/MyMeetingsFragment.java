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

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;
import com.example.meetapp.ui.Views.CalenderBarPackage.CalenderBarFragment;
import com.example.meetapp.ui.meetings.SelectDateFragment;
import com.example.meetapp.ui.meetings.meetingInfo.MeetingInfoFragment;

public class MyMeetingsFragment extends Fragment implements OnClickInRecyclerView {

    private MyMeetingsViewModel mViewModel;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_meetings_fragment, container, false);

        CalenderBarFragment calenderBarFragment = new CalenderBarFragment(mViewModel,this);
        getChildFragmentManager().beginTransaction().replace(R.id.calender_bar_fragment_container, calenderBarFragment).commit();

        SelectDateFragment selectDateFragment = new SelectDateFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container,selectDateFragment).commit();

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
        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        mViewModel = ViewModelProviders.of(this).get(MyMeetingsViewModel.class);
        mViewModel.init(mainActivityViewModel,null);
    }


    @Override
    public void onClickInRecyclerView(Object value, String action) {
        if (action.equals("None")){
            getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container, new SelectDateFragment()).commit();
        }
        else if (action.equals("Public")) {
            MeetingInfoFragment meetingInfoFragment = new MeetingInfoFragment();
            Meeting meeting = mViewModel.getMeetings().getValue().get(((int)value)-1).getValue();
            Bundle bundle = new Bundle();
            bundle.putString("id",meeting.getId());
            String type = meeting instanceof Meeting ? "Public" : "Group";
            bundle.putString("type", type);
            String groupId = meeting instanceof Meeting ? "Public" : meeting.getId();
            bundle.putString("groupId", groupId);
            meetingInfoFragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container, meetingInfoFragment).commit();
        }
        else if (action.equals("Group")){
            MeetingInfoFragment meetingInfoFragment = new MeetingInfoFragment();
            Meeting meeting = mViewModel.getGroupMeetings().getValue().get(((int)value)-1).getValue();
            Bundle bundle = new Bundle();
            bundle.putString("id",meeting.getId());
            String type = meeting instanceof Meeting ? "Public" : "Group";
            bundle.putString("type", type);
            String groupId = meeting instanceof Meeting ? "Public" : meeting.getId();
            bundle.putString("groupId", groupId);
            meetingInfoFragment.setArguments(bundle);
            getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container, meetingInfoFragment).commit();
        }

    }
}