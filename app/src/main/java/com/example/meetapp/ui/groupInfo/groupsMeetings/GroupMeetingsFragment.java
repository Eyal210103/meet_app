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
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.ui.calenderBarPackage.CalenderBarFragment;
import com.example.meetapp.ui.groupInfo.GroupInfoFragment;
import com.example.meetapp.ui.groupInfo.GroupInfoViewModel;
import com.example.meetapp.ui.meetings.SelectDateFragment;
import com.example.meetapp.ui.meetings.meetingInfo.MeetingInfoFragment;

public class GroupMeetingsFragment extends Fragment implements OnClickInRecyclerView {

    private GroupInfoViewModel mViewModel;


    private GroupInfoFragment parent;
    public void setParent(GroupInfoFragment parent) {
        this.parent = parent;
        mViewModel = ViewModelProviders.of(parent).get(GroupInfoViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mViewModel == null){
            mViewModel = ViewModelProviders.of(parent).get(GroupInfoViewModel.class);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.meeting_info_fragment_container_group, new SelectDateFragment()).commit();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_meetings_fragment, container, false);

        CalenderBarFragment calenderBarFragment = new CalenderBarFragment(mViewModel, this);
        getChildFragmentManager().beginTransaction().replace(R.id.calender_bar_fragment_container_group, calenderBarFragment).commit();
        return view;
    }

    @Override
    public void onClickInRecyclerView(Object value, String action, int i) {
        if (value == null){
            getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_rtl,R.anim.page_transition_slide_ltr)
                    .replace(R.id.meetingInfo_fragment_container, new SelectDateFragment()).commit();
        }else {
            if (action.equals("None")) {
               getChildFragmentManager().beginTransaction().setCustomAnimations(R.anim.page_transition_slide_rtl,R.anim.page_transition_slide_ltr)
                       .replace(R.id.meeting_info_fragment_container_group, new SelectDateFragment()).commit();
            } else {
                MeetingInfoFragment meetingInfoFragment = new MeetingInfoFragment();
                GroupMeeting meeting = (GroupMeeting) mViewModel.getMeetings().getValue().get((String) value).get(i).getValue();
                Bundle bundle = new Bundle();
                bundle.putString(Const.BUNDLE_ID, meeting.getId());
                bundle.putString(Const.BUNDLE_TYPE, Const.MEETING_TYPE_GROUP);
                String groupId = ((GroupMeeting) meeting).getGroupId();
                bundle.putString(Const.BUNDLE_GROUP_ID, groupId);
                meetingInfoFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().replace(R.id.meeting_info_fragment_container_group, meetingInfoFragment).commit();
            }
        }
    }
}