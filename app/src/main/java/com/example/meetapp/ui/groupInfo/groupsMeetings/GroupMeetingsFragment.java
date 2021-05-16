package com.example.meetapp.ui.groupInfo.groupsMeetings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInCalender;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.ui.calenderBar.CalenderBarFragment;
import com.example.meetapp.ui.groupInfo.GroupInfoFragment;
import com.example.meetapp.ui.groupInfo.GroupInfoViewModel;
import com.example.meetapp.ui.meetings.SelectDateFragment;
import com.example.meetapp.ui.meetings.meetingInfo.MeetingInfoFragment;

import java.util.Calendar;

public class GroupMeetingsFragment extends Fragment implements OnClickInCalender {

    private GroupInfoViewModel mViewModel;
    private long prevMillis;
    private GroupInfoFragment parent;

    public void setParent(GroupInfoFragment parent) {
        this.parent = parent;
        mViewModel = ViewModelProviders.of(parent).get(GroupInfoViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(parent).get(GroupInfoViewModel.class);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.meeting_info_fragment_container_group, new SelectDateFragment()).commit();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_meetings_fragment, container, false);
        prevMillis = Calendar.getInstance().getTimeInMillis();

        CalenderBarFragment calenderBarFragment = new CalenderBarFragment(mViewModel, this);
        getChildFragmentManager().beginTransaction().replace(R.id.calender_bar_fragment_container_group, calenderBarFragment).commit();
        return view;
    }

    @Override
    public void onClickIInCalender(Object value, String action, long millis, int i) {
        if (action.equals("None") || value == null) {
            if (millis < prevMillis) {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_right_enter, R.anim.page_transition_slide_right_exit)
                        .replace(R.id.meeting_info_fragment_container_group, new SelectDateFragment()).commit();
            } else {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_left_enter, R.anim.page_transition_slide_left_exit)
                        .replace(R.id.meeting_info_fragment_container_group, new SelectDateFragment()).commit();
            }
            prevMillis = millis;
        } else {
            MeetingInfoFragment meetingInfoFragment = new MeetingInfoFragment();
            GroupMeeting meeting = (GroupMeeting) mViewModel.getMeetings().getValue().get((String) value).get(i).getValue();
            Bundle bundle = new Bundle();
            bundle.putString(Const.BUNDLE_ID, meeting.getId());
            bundle.putString(Const.BUNDLE_TYPE, Const.MEETING_TYPE_GROUP);
            String groupId = ((GroupMeeting) meeting).getGroupId();
            bundle.putString(Const.BUNDLE_GROUP_ID, groupId);
            meetingInfoFragment.setArguments(bundle);
            if (meeting.getMillis() < prevMillis) {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_right_enter,R.anim.page_transition_slide_right_exit)
                        .replace(R.id.meeting_info_fragment_container_group, meetingInfoFragment).commit();
            }else {
                getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_left_enter,R.anim.page_transition_slide_left_exit)
                        .replace(R.id.meeting_info_fragment_container_group, meetingInfoFragment).commit();
            }
        }
    }
}