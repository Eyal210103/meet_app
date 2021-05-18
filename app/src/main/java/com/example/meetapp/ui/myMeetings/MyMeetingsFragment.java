package com.example.meetapp.ui.myMeetings;

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
import com.example.meetapp.callbacks.OnClickInCalender;
import com.example.meetapp.databinding.MyMeetingsFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;
import com.example.meetapp.ui.calenderBar.CalenderBarFragment;
import com.example.meetapp.ui.meetings.SelectDateFragment;
import com.example.meetapp.ui.meetings.meetingInfo.MeetingInfoFragment;

import java.util.Calendar;

public class MyMeetingsFragment extends Fragment implements OnClickInCalender {

    private MyMeetingsViewModel mViewModel;

    private long prevMillis;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        mViewModel = ViewModelProviders.of(this).get(MyMeetingsViewModel.class);
        mViewModel.init(mainActivityViewModel);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        MyMeetingsFragmentBinding binding = MyMeetingsFragmentBinding.inflate(inflater, container, false);

        CalenderBarFragment calenderBarFragment;
        if (getArguments() != null) {
            if (getArguments().containsKey(Const.BUNDLE_GOTO_DATE)) {
                String goTo = getArguments().getString(Const.BUNDLE_GOTO_DATE);
                calenderBarFragment = new CalenderBarFragment(mViewModel, goTo, this);
            }
            else {
                calenderBarFragment = new CalenderBarFragment(mViewModel, this);
            }
        } else {
            calenderBarFragment = new CalenderBarFragment(mViewModel, this);
        }

        getChildFragmentManager().beginTransaction().replace(R.id.calender_bar_fragment_container, calenderBarFragment).commit();

        prevMillis = Calendar.getInstance().getTimeInMillis();

        SelectDateFragment selectDateFragment = new SelectDateFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.meetingInfo_fragment_container, selectDateFragment).commit();

        binding.myMeetingsCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_myMeetingsFragment_to_createMeetingFragment);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onClickIInCalender(Object value, String action, long millis, int i) {
        if (action.equals("None") || value == null) {
            if (millis < prevMillis) {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_right_enter, R.anim.page_transition_slide_right_exit)
                        .replace(R.id.meetingInfo_fragment_container, new SelectDateFragment()).commit();
            } else {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_left_enter, R.anim.page_transition_slide_left_exit)
                        .replace(R.id.meetingInfo_fragment_container, new SelectDateFragment()).commit();
            }
            prevMillis = millis;
        } else {
            MeetingInfoFragment meetingInfoFragment = new MeetingInfoFragment();
            String key = (String) value;
            Meeting meeting = mViewModel.getMeetings().getValue().get(key).get(i).getValue();
            assert meeting != null;
            if (mViewModel.getIdsOfGroupMeetingsToGroup().containsKey(meeting.getId())) {
                Bundle bundle = new Bundle();
                bundle.putString(Const.BUNDLE_ID, meeting.getId());
                bundle.putString(Const.BUNDLE_TYPE, Const.MEETING_TYPE_GROUP);
                String groupId = mViewModel.getIdsOfGroupMeetingsToGroup().get(meeting.getId());
                bundle.putString(Const.BUNDLE_GROUP_ID, groupId);
                meetingInfoFragment.setArguments(bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Const.BUNDLE_ID, meeting.getId());
                bundle.putString(Const.BUNDLE_TYPE, Const.MEETING_TYPE_PUBLIC);
                bundle.putString(Const.BUNDLE_GROUP_ID, "");
                meetingInfoFragment.setArguments(bundle);
            }

            if (meeting.getMillis() < prevMillis) {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_right_enter, R.anim.page_transition_slide_right_exit)
                        .replace(R.id.meetingInfo_fragment_container, meetingInfoFragment).commit();
            } else {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_left_enter, R.anim.page_transition_slide_left_exit)
                        .replace(R.id.meetingInfo_fragment_container, meetingInfoFragment).commit();
            }
            prevMillis = meeting.getMillis();
        }


    }

}