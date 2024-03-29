package com.example.meetapp.ui.createMeeting;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnClickInRecyclerView;
import com.example.meetapp.callbacks.OnDismissPlacePicker;
import com.example.meetapp.chatPushNotification.CreateMeetingService;
import com.example.meetapp.databinding.FragmentCreateMeetingBinding;
import com.example.meetapp.firebaseActions.FirebaseTags;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.receivers.MeetingReminderNotificationBroadcastReceiver;
import com.example.meetapp.ui.MainActivityViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateMeetingFragment extends Fragment implements OnDismissPlacePicker, OnClickInRecyclerView {

    private MainActivityViewModel mainActivityViewModel;
    private DatePickerDialog datePickerDialog;
    private LatLng location;
    private boolean isGroup;
    private GridLayoutManager gridLayoutManager;
    private int position;
    private FragmentCreateMeetingBinding binding;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityViewModel = ViewModelProviders.of(requireActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCreateMeetingBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        RecyclerView recyclerViewSubjects = binding.createMeetingRecyclerView;
        SubjectAdapter subjectAdapter = new SubjectAdapter(this);
        recyclerViewSubjects.setAdapter(subjectAdapter);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 5);
        recyclerViewSubjects.setLayoutManager(gridLayoutManager);

        isGroup = false;
        position = -1;


        SpinnerGroupAdapter spinnerAdapter = new SpinnerGroupAdapter(requireActivity(), mainActivityViewModel.getGroups().getValue());
        binding.createSelectGroupSpinner.setAdapter(spinnerAdapter);


        RadioGroup radioGroup = binding.radioGroup;
        radioGroup.check(R.id.radio_meeting);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_group) {
                    ViewGroup.LayoutParams layoutParams = binding.createSelectGroupSpinner.getLayoutParams();
                    layoutParams.height = 200;
                    binding.createSelectGroupSpinner.setLayoutParams(layoutParams);
                    binding.isPublicGroupMeetingSwitch.setVisibility(View.VISIBLE);
                    binding.isPublicTextView.setVisibility(View.VISIBLE);
                    isGroup = true;
                } else if (checkedId == R.id.radio_meeting) {
                    ViewGroup.LayoutParams layoutParams = binding.createSelectGroupSpinner.getLayoutParams();
                    layoutParams.height = 0;
                    binding.isPublicGroupMeetingSwitch.setVisibility(View.INVISIBLE);
                    binding.isPublicTextView.setVisibility(View.INVISIBLE);
                    isGroup = false;
                    binding.createSelectGroupSpinner.setLayoutParams(layoutParams);
                }
            }
        });

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        updateDateUI(c);

        binding.createMeetingChooseTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        TimePickerDialog mTimePicker = new TimePickerDialog(CreateMeetingFragment.this.requireActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), selectedHour, selectedMinute);
                updateDateUI(c);
            }
        }, mHour, minutes, true);

        datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
                mTimePicker.show();
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

        binding.createMeetingChooseLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePickerDialog placePickerDialog = new PlacePickerDialog(CreateMeetingFragment.this);
                placePickerDialog.show(getFragmentManager(), "placePicker");
            }
        });

        binding.createMeetingCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isGroup) {
                    GroupMeeting meeting = new GroupMeeting();
                    meeting.setMillis(c.getTimeInMillis());
                    meeting.setLatitude(location.latitude);
                    meeting.setLongitude(location.longitude);
                    meeting.setDescription(binding.createMeetingDescriptionEt.getText().toString());
                    meeting.setSubject(subjectAdapter.getSelected());
                    String gId = ((MutableLiveData<Group>) binding.createSelectGroupSpinner.getSelectedItem()).getValue().getId();
                    boolean isOpen = binding.isPublicGroupMeetingSwitch.isChecked();
                    meeting.setOpen(isOpen);
                    meeting.setGroupId(gId);
                    meeting.updateOrAddToDatabase();
                    meeting.confirmUserArrival(CurrentUser.getInstance().getId());
                    CurrentUser.joinMeeting(meeting.getId(), FirebaseTags.GROUP_MEETINGS_CHILDES, gId);
                    MeetingReminderNotificationBroadcastReceiver.setAlarm(CreateMeetingFragment.this.requireContext(),meeting);
                } else {
                    Meeting meeting = new Meeting();
                    meeting.setMillis(c.getTimeInMillis());
                    meeting.setLatitude(location.latitude);
                    meeting.setLongitude(location.longitude);
                    meeting.setDescription(binding.createMeetingDescriptionEt.getText().toString());
                    meeting.setSubject(subjectAdapter.getSelected());
                    meeting.updateOrAddToDatabase();
                    meeting.confirmUserArrival(CurrentUser.getInstance().getId());
                    CurrentUser.joinMeeting(meeting.getId(), FirebaseTags.PUBLIC_MEETINGS_CHILDES, meeting.getId());
                    MeetingReminderNotificationBroadcastReceiver.setAlarm(CreateMeetingFragment.this.requireContext(),meeting);
                }
                Intent intent = new Intent(requireContext(),CreateMeetingService.class);
                intent.putExtra(Const.BUNDLE_MEETING,c.getTimeInMillis());
                requireActivity().startService(intent);

                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_createMeetingFragment_to_socialMenuFragment);
            }
        });

        if (getArguments() != null && getArguments().getString(Const.BUNDLE_GROUP_ID) != null) {
            String groupId = getArguments().getString(Const.BUNDLE_GROUP_ID);
            int index = spinnerAdapter.getIndex(groupId);
            if (index != -1) {
                binding.createSelectGroupSpinner.setSelection(index);
                radioGroup.check(R.id.radio_group);
                binding.createSelectGroupSpinner.setEnabled(false);
                radioGroup.setEnabled(false);
            }
        }

        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateDateUI(Calendar c){
        binding.createMeetingTvDayCalendarItem.setText(getMonth(c.get(Calendar.MONTH)));
        binding.createMeetingTvDayOfMonthCalendarItem.setText("" + (c.get(Calendar.DAY_OF_MONTH)));
        binding.createMeetingHourTextView.setText(String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
        binding.createMeetingTvDayOfWeekCalendarItem.setText(getDayOfWeek(c.get(Calendar.DAY_OF_WEEK)));

    }

    public String getMonth(int day){
        switch (day){
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

    public String getDayOfWeek(int day){
        switch (day){
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

    private String getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude,location.longitude,1);
            Address obj = addresses.get(0);
            return obj.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Null";
    }

    @Override
    public void getSelectedLocation(LatLng latLng) {
        location = latLng;
        binding.createMeetingLocationTextView.setText(getAddress(latLng));
    }

    @Override
    public void onClickInRecyclerView(Object value, String action, Integer i) {
        if (action.equals(Const.ACTION_SUBJECT)){
            int v = (int)value;
            if (position != -1){
                gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.subject_background);
            }
            position = v;
            gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.selected_subject_background);
        }
    }
}