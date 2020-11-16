package com.example.meetapp.ui.createMeeting;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnDismissPlacePicker;
import com.example.meetapp.ui.MainActivityViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateMeetingFragment extends Fragment implements OnDismissPlacePicker {

    MainActivityViewModel mainActivityViewModel;
    View view;
    Spinner SpinnerSelectGroup;
    DatePickerDialog datePickerDialog;
    LatLng location;
    TextView locationTV;

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
        recyclerViewSubjects.setAdapter(new SubjectAdapter(requireActivity()));
        recyclerViewSubjects.setLayoutManager(new GridLayoutManager(requireActivity(), 5));

        SpinnerSelectGroup = view.findViewById(R.id.create_select_group_recyclerView);
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(requireActivity(),R.layout.select_group_adapter,mainActivityViewModel.getGroups().getValue());
        SpinnerSelectGroup.setAdapter(spinnerAdapter);

        locationTV = view.findViewById(R.id.create_meeting_location_textView);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_group){
                    ViewGroup.LayoutParams layoutParams = SpinnerSelectGroup.getLayoutParams();
                    layoutParams.height = 200;
                    SpinnerSelectGroup.setLayoutParams(layoutParams);

                }else if (checkedId == R.id.radio_meeting){
                    ViewGroup.LayoutParams layoutParams = SpinnerSelectGroup.getLayoutParams();
                    layoutParams.height = 0;
                    SpinnerSelectGroup.setLayoutParams(layoutParams);

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

        return view;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void updateDateUI(Calendar c , View view){
        ((TextView)view.findViewById(R.id.create_meeting_tv_day_calendar_item)).setText(getThreeLetterDay(c.get(Calendar.DAY_OF_WEEK)));
        ((TextView)view.findViewById(R.id.create_meeting_tv_day_of_month_calendar_item)).setText("" + (c.get(Calendar.DAY_OF_MONTH)));
        ((TextView)view.findViewById(R.id.create_meeting_hour_textView)).setText(String.format("%2d:%2d", c.get(Calendar.HOUR), c.get(Calendar.MINUTE)));

    }
    public String getThreeLetterDay(int day){
        switch (day){
            case android.icu.util.Calendar.SUNDAY:
                return "SUN";

            case android.icu.util.Calendar.MONDAY:
                return "MON";

            case android.icu.util.Calendar.TUESDAY:
                return "TUE";

            case android.icu.util.Calendar.WEDNESDAY:
                return "WED";

            case android.icu.util.Calendar.THURSDAY:
                return "THU";

            case android.icu.util.Calendar.FRIDAY:
                return "FRI";

            case android.icu.util.Calendar.SATURDAY:
                return "SAT";

            default:
                return "";
        }
    }

    @Override
    public void getSelectedLocation(LatLng latLng) {
        location = latLng;
        locationTV.setText(getAddress(latLng));
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

}