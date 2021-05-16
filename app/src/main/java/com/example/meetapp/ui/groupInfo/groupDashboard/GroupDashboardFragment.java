package com.example.meetapp.ui.groupInfo.groupDashboard;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.databinding.GroupDashboardFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.Message;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;
import com.example.meetapp.ui.groupInfo.GroupInfoFragment;
import com.example.meetapp.ui.groupInfo.GroupInfoViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GroupDashboardFragment extends Fragment {

    private GroupInfoViewModel mViewModel;
    private GroupInfoFragment parent;
    private TextView lastMessageTextView;
    private DashMembersAdapter adapter;
    private String id;
    private GroupDashboardFragmentBinding binding;


    public void setParent(Fragment fragment) {
        parent = (GroupInfoFragment) fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(parent).get(GroupInfoViewModel.class);
        MainActivityViewModel mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
        this.id = requireArguments().getString(Const.BUNDLE_GROUP_ID);
        String groupId = requireArguments().getString(Const.BUNDLE_GROUP_ID);
        mViewModel.init(mainActivityViewModel.getGroupsMap().get(groupId),id);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = GroupDashboardFragmentBinding.inflate(inflater,container,false);
        View view = binding.getRoot();


        lastMessageTextView = binding.include2.dashLastMessageTextView;
        lastMessageTextView.setText(getResources().getString(R.string.no_messages_alert));

        RecyclerView recyclerView = binding.include3.groupDashMembersRecycler;
        adapter = new DashMembersAdapter(this, mViewModel.getMembersLiveData().getValue());
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(requireActivity(), 3);
        recyclerView.setLayoutManager(glm);


        binding.groupDashCreateMeetingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BUNDLE_GROUP_ID,id);
                navController.navigate(R.id.action_groupInfoFragment_to_createMeetingFragment,bundle);
            }
        });


        View message = binding.include2.getRoot();
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent != null)
                    parent.swipeToChat();
            }
        });

        View meeting = binding.include.getRoot();
        meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parent != null)
                    parent.swipeToMeetings();
            }
        });

        mViewModel.getMembersLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<User>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<User>> mutableLiveData) {
                adapter.notifyDataSetChanged();
                for (MutableLiveData<User> u : mutableLiveData) {
                    u.observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        mViewModel.getClosesMeeting().observe(getViewLifecycleOwner(), new Observer<GroupMeeting>() {
            @Override
            public void onChanged(GroupMeeting groupMeeting) {
                updateClosesMeetingUI(groupMeeting);
            }
        });


        mViewModel.getLastMessage().observe(getViewLifecycleOwner(), new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                String m;
                if (message.getContext().length() > 15)
                    m = message.getSenderDisplayName() + ":\n" + message.getContext().substring(0, 15) + "...";
                else
                    m = message.getSenderDisplayName() + ":\n" + message.getContext();
                lastMessageTextView.setText(m);
            }
        });
        return view;
    }

    @SuppressLint("DefaultLocale")
    private void updateClosesMeetingUI(Meeting meeting){
        binding.include.getRoot().setVisibility(View.VISIBLE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(meeting.getMillis()));
        binding.include.groupDashDayOfMonthTextView.setText(String.format("%d", calendar.get(Calendar.DAY_OF_MONTH)));
        binding.include.groupDashHourTextView.setText(String.format("%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        binding.include.groupDashMonthTextView.setText(getMonth(calendar.get(Calendar.MONTH)));
        LatLng latLng = new LatLng(meeting.getLatitude(),meeting.getLongitude());
        binding.include.groupDashMeetingLocationTextView.setText(getAddress(latLng));
        binding.include.groupDashSubjectImageView.setImageResource(getSubjectIcon(meeting.getSubject()));
    }

    public String getMonth(int month) {
        switch (month) {
            case android.icu.util.Calendar.JANUARY:
                return getString(R.string.months_january);

            case android.icu.util.Calendar.FEBRUARY:
                return getString(R.string.months_february);

            case android.icu.util.Calendar.MARCH:
                return getString(R.string.months_march);

            case android.icu.util.Calendar.APRIL:
                return getString(R.string.months_april);

            case android.icu.util.Calendar.MAY:
                return getString(R.string.months_may);

            case android.icu.util.Calendar.JUNE:
                return getString(R.string.months_june);

            case android.icu.util.Calendar.JULY:
                return getString(R.string.months_july);

            case android.icu.util.Calendar.AUGUST:
                return getString(R.string.months_august);

            case android.icu.util.Calendar.SEPTEMBER:
                return getString(R.string.months_september);

            case android.icu.util.Calendar.OCTOBER:
                return getString(R.string.months_october);

            case android.icu.util.Calendar.NOVEMBER:
                return getString(R.string.months_november);

            case android.icu.util.Calendar.DECEMBER:
                return getString(R.string.months_december);

            default:
                return "";
        }
    }

    private String getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude,location.longitude,1);
            Address obj = addresses.get(0);
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();
            return obj.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Null";
    }

    private int getSubjectIcon(String subject){
        switch (subject){
            case Const.SUBJECT_RESTAURANT:
                return R.drawable.restaurant;
            case Const.SUBJECT_BASKETBALL:
                return R.drawable.basketball;
            case Const.SUBJECT_SOCCER:
                return R.drawable.soccer;
            case Const.SUBJECT_FOOTBALL:
                return R.drawable.football;
            case Const.SUBJECT_VIDEO_GAMES:
                return R.drawable.videogame;
            case Const.SUBJECT_MEETING:
                return R.drawable.meetingicon;
            default:
                return R.drawable.groupsicon;
        }
    }
}