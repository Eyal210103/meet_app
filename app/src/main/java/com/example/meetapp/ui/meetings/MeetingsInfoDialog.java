package com.example.meetapp.ui.meetings;

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

import com.example.meetapp.R;
import com.example.meetapp.model.meetings.Meeting;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingsInfoDialog extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.meeting_info_dialog, container, false);

        Meeting meeting = (Meeting) getArguments().getSerializable("meeting");

        TextView day = view.findViewById(R.id.meeting_dialog_tv_day_of_month_calendar_item);
        TextView dayOfWeek = view.findViewById(R.id.meeting_dialog_tv_day_of_week_calendar_item);
        TextView month = view.findViewById(R.id.meeting_dialog_tv_day_calendar_item);
        TextView hour = view.findViewById(R.id.meeting_dialog_hour_textView);
        TextView location = view.findViewById(R.id.meeting_dialog_location_textView);
        TextView description = view.findViewById(R.id.meeting_dialog_desc_textView);

        Date date = new Date(meeting.getMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        day.setText(calendar.get(Calendar.DAY_OF_MONTH));
        dayOfWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        month.setText(getThreeLetterMonth(calendar.get(Calendar.MONTH)));
        hour.setText(String.format("%2d:%2d", calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE)));
        description.setText(meeting.getDescription());
        location.setText(meeting.getSubject() + "\n" + getAddress(meeting.getLocation()));

        return view;
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
    public String getAddress(LatLng location) {
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude,location.longitude,1);
            try {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                return add;
            }catch (Exception e){
                e.printStackTrace();
            }

            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            //add = add + "\n" + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Null";
    }

}
