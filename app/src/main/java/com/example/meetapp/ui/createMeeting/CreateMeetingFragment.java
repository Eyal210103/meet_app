package com.example.meetapp.ui.createMeeting;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.Group;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.MainActivityViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateMeetingFragment extends Fragment implements OnDismissPlacePicker, OnClickInRecyclerView {

    MainActivityViewModel mainActivityViewModel;
    View view;
    Spinner spinnerSelectGroup;
    DatePickerDialog datePickerDialog;
    LatLng location;
    TextView locationTV;
    boolean isGroup;
    GridLayoutManager gridLayoutManager;
    int position;

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
        SubjectAdapter subjectAdapter = new SubjectAdapter(this);
        recyclerViewSubjects.setAdapter(subjectAdapter);
        gridLayoutManager = new GridLayoutManager(requireActivity(), 5);
        recyclerViewSubjects.setLayoutManager(gridLayoutManager);

        isGroup = false;
        position = -1;

        spinnerSelectGroup = view.findViewById(R.id.create_select_group_recyclerView);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(requireActivity(),R.layout.select_group_adapter,mainActivityViewModel.getGroups().getValue());
        spinnerSelectGroup.setAdapter(spinnerAdapter);

        locationTV = view.findViewById(R.id.create_meeting_location_textView);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.check(R.id.radio_meeting);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_group){
                    ViewGroup.LayoutParams layoutParams = spinnerSelectGroup.getLayoutParams();
                    layoutParams.height = 200;
                    spinnerSelectGroup.setLayoutParams(layoutParams);
                    isGroup = true;

                }else if (checkedId == R.id.radio_meeting){
                    ViewGroup.LayoutParams layoutParams = spinnerSelectGroup.getLayoutParams();
                    layoutParams.height = 0;
                    spinnerSelectGroup.setLayoutParams(layoutParams);
                }
            }
        });


        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);

        view.findViewById(R.id.create_meeting_choose_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });
        TimePickerDialog mTimePicker = new TimePickerDialog(CreateMeetingFragment.this.requireActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH) ,selectedHour,selectedMinute);
                Toast.makeText(requireActivity(), ""+ c.get(Calendar.MONTH) + " " + c.get(Calendar.MINUTE), Toast.LENGTH_SHORT).show();
                updateDateUI(c,view);
            }
        }, mHour, minutes, true);
        datePickerDialog = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
                mTimePicker.show();
            }
        }, mYear, mMonth, mDay);

        view.findViewById(R.id.create_meeting_choose_location_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePickerDialog placePickerDialog = new PlacePickerDialog(CreateMeetingFragment.this);
                placePickerDialog.show(getFragmentManager(),"placePicker");
            }
        });

        view.findViewById(R.id.create_meeting_complete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isGroup){
                    GroupMeeting meeting = new GroupMeeting();
                    meeting.setMillis(c.getTimeInMillis());
                    meeting.setLatitude(location.latitude);
                    meeting.setLongitude(location.longitude);
                    meeting.setDescription(((EditText)view.findViewById(R.id.create_meeting_description_et)).getText().toString());
                    meeting.setSubject(subjectAdapter.getSelected());
                    String gId = ((MutableLiveData<Group>)spinnerSelectGroup.getSelectedItem()).getValue().getId();
                    meeting.setGroupId(gId);
                    meeting.updateOrAddReturnId();
                    CurrentUser.joinMeeting(meeting.getId(),"Group" ,gId);
                }else {
                    Meeting meeting = new Meeting();
                    meeting.setMillis(c.getTimeInMillis());
                    meeting.setLatitude(location.latitude);
                    meeting.setLongitude(location.longitude);
                    meeting.setDescription(((EditText)view.findViewById(R.id.create_meeting_description_et)).getText().toString());
                    meeting.setSubject(subjectAdapter.getSelected());
                    meeting.updateOrAddReturnId();
                    CurrentUser.joinMeeting(meeting.getId(),"Public",meeting.getId());
                }
                Bundle bundle = new Bundle();
                bundle.putString("action", "meetings");
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_createMeetingFragment_to_socialMenuFragment, bundle);
            }
        });
        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateDateUI(Calendar c , View view){
        ((TextView)view.findViewById(R.id.create_meeting_tv_day_calendar_item)).setText(getThreeLetterMonth(c.get(Calendar.MONTH)));
        ((TextView)view.findViewById(R.id.create_meeting_tv_day_of_month_calendar_item)).setText("" + (c.get(Calendar.DAY_OF_MONTH)));
        ((TextView)view.findViewById(R.id.create_meeting_hour_textView)).setText(String.format("%2d:%2d", c.get(Calendar.HOUR), c.get(Calendar.MINUTE)));
        ((TextView)view.findViewById(R.id.create_meeting_tv_day_of_week_calendar_item)).setText(getDayOfWeek(c.get(Calendar.DAY_OF_WEEK)));

    }

    public String getThreeLetterMonth(int day){
        switch (day){
            case android.icu.util.Calendar.JANUARY:
                return "JAN";

            case android.icu.util.Calendar.FEBRUARY:
                return "FEB";

            case android.icu.util.Calendar.MARCH:
                return "MAR";

            case android.icu.util.Calendar.APRIL:
                return "APR";

            case android.icu.util.Calendar.MAY:
                return "MAY";

            case android.icu.util.Calendar.JUNE:
                return "JUN";

            case android.icu.util.Calendar.JULY:
                return "JUL";

            case android.icu.util.Calendar.AUGUST:
                return "AUG";

            case android.icu.util.Calendar.SEPTEMBER:
                return "SEP";

            case android.icu.util.Calendar.OCTOBER:
                return "OCT";

            case android.icu.util.Calendar.NOVEMBER:
                return "NOV";

            case android.icu.util.Calendar.DECEMBER:
                return "DEC";

            default:
                return "";
        }
    }

    public String getDayOfWeek(int day){
        switch (day){
            case android.icu.util.Calendar.SUNDAY:
                return "Sunday";

            case android.icu.util.Calendar.MONDAY:
                return "Monday";

            case android.icu.util.Calendar.TUESDAY:
                return "Tuesday";

            case android.icu.util.Calendar.WEDNESDAY:
                return "Wednesday";

            case android.icu.util.Calendar.THURSDAY:
                return "Thursday";

            case android.icu.util.Calendar.FRIDAY:
                return "Friday";

            case android.icu.util.Calendar.SATURDAY:
                return "Saturday";
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

    @Override
    public void getSelectedLocation(LatLng latLng) {
        location = latLng;
        Log.d("----------------------------------///////////", "getSelectedLocation: " + latLng.toString());
        locationTV.setText(getAddress(latLng));
    }

    @Override
    public void onClickInRecyclerView(Object value, String action) {
        if (action.equals("subject")){
            int v = (int)value;
            if (position != -1){
                gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.subject_background);
            }
            position = v;
            gridLayoutManager.findViewByPosition(position).setBackgroundResource(R.drawable.selected_subject_background);
        }
    }
}