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
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;
import com.example.meetapp.ui.Views.CalenderBarPackage.CalenderBarFragment;
import com.example.meetapp.ui.meetings.SelectDateFragment;
import com.example.meetapp.ui.meetings.meetingInfo.MeetingInfoFragment;

public class MyMeetingsFragment extends Fragment implements OnClickInRecyclerView {

    private MyMeetingsViewModel mViewModel;
    private CalenderBarFragment calenderBarFragment;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_meetings_fragment, container, false);

        calenderBarFragment = new CalenderBarFragment(mViewModel,this);
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
        else {
            MeetingInfoFragment meetingInfoFragment = new MeetingInfoFragment();
            if (value == null) {
                getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container, new SelectDateFragment()).commit();
                return;
            }
            String key = (String) value;
            if (action.equals("Group")) {
                GroupMeeting meeting = (GroupMeeting) mViewModel.getMeetings().getValue().get(key).getValue();
                Bundle bundle = new Bundle();
                bundle.putString("id", meeting.getId());
                bundle.putString("type", "Group");
                String groupId = ((GroupMeeting) meeting).getGroupId();
                bundle.putString("groupId", groupId);
                meetingInfoFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container, meetingInfoFragment).commit();
            }
            else if (action.equals("Public")) {
                Meeting meeting = mViewModel.getMeetings().getValue().get(key).getValue();
                Bundle bundle = new Bundle();
                bundle.putString("id", meeting.getId());
                bundle.putString("type", "Public");
                bundle.putString("groupId", "");
                meetingInfoFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container, meetingInfoFragment).commit();
            }
        }


    }
}