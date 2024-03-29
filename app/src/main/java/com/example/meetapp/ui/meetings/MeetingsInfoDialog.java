package com.example.meetapp.ui.meetings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.databinding.MeetingInfoDialogBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.Meeting;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingsInfoDialog extends DialogFragment {

    MeetingInfoDialogViewModel mViewModel;
    String groupId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MeetingInfoDialogViewModel.class);
        Meeting meeting = (Meeting) getArguments().getSerializable(Const.BUNDLE_MEETING);
        groupId = getArguments().getString(Const.BUNDLE_GROUP_ID);
        mViewModel.init(meeting.getId());
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        MeetingInfoDialogBinding binding = MeetingInfoDialogBinding.inflate(inflater, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Meeting meeting = (Meeting) getArguments().getSerializable(Const.BUNDLE_MEETING);

        TextView day = binding.meetingDialogTvDayOfMonthCalendarItem;
        TextView dayOfWeek = binding.meetingDialogTvDayOfWeekCalendarItem;
        TextView month = binding.meetingDialogTvDayCalendarItem;
        TextView hour = binding.meetingDialogHourTextView;
        TextView location = binding.meetingDialogLocationTextView;
        TextView description = binding.meetingDialogDescTextView;

        Date date = new Date(meeting.getMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        day.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
        dayOfWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        month.setText(getMonth(calendar.get(Calendar.MONTH)));
        hour.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        description.setText(meeting.getDescription());
        location.setText(meeting.getSubject() + "\n\n" + getAddress(meeting.getLocation()));


        RecyclerView recyclerView = binding.meetingDialogWhoComingRecycler;
        WhoComingAdapter adapter = new WhoComingAdapter(requireActivity(), mViewModel.users.getValue());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        binding.meetingInfoDialogMoreInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString(Const.BUNDLE_ID, meeting.getId());
                String type = groupId.equals("") ? Const.MEETING_TYPE_PUBLIC : Const.MEETING_TYPE_GROUP;
                bundle.putString(Const.BUNDLE_TYPE, type);
                bundle.putString(Const.BUNDLE_GROUP_ID, groupId);
                navController.navigate(R.id.action_homeFragment_to_meetingInfoFragment, bundle);
            }
        });

        mViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                adapter.notifyDataSetChanged();
            }
        });


        return binding.getRoot();
    }


    public String getDayOfWeek(int day) {
        switch (day) {
            case android.icu.util.Calendar.SUNDAY:
                return getString(R.string.days_sunday);

            case android.icu.util.Calendar.MONDAY:
                return getString(R.string.days_monday);

            case android.icu.util.Calendar.TUESDAY:
                return getString(R.string.days_tuesday);

            case android.icu.util.Calendar.WEDNESDAY:
                return getString(R.string.days_wednesday);

            case android.icu.util.Calendar.THURSDAY:
                return getString(R.string.days_thursday);

            case android.icu.util.Calendar.FRIDAY:
                return getString(R.string.days_friday);

            case android.icu.util.Calendar.SATURDAY:
                return getString(R.string.days_saturday);
            default:
                return "";
        }
    }

    public String getMonth(int day) {
        switch (day) {
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

    public String getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            try {
                Address obj = addresses.get(0);
                return obj.getAddressLine(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "An Error Has Occurred";
    }

}
