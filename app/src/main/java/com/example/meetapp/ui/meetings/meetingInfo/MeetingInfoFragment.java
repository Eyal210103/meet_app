package com.example.meetapp.ui.meetings.meetingInfo;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.firebaseActions.FirebaseTags;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.CurrentUser;
import com.example.meetapp.model.User;
import com.example.meetapp.model.meetings.GroupMeeting;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.meetings.WhoComingAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingInfoFragment extends Fragment {

    private MeetingInfoViewModel mViewModel;
    private String type;
    private View view;
    private MapView mapView;
    private GoogleMap mMap;
    private boolean isUserAlreadyIn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MeetingInfoViewModel.class);
        String id  = getArguments().getString(Const.BUNDLE_ID);
        type = getArguments().getString(Const.BUNDLE_TYPE);
        String groupId = getArguments().getString(Const.BUNDLE_GROUP_ID,"");
        mViewModel.init(id,groupId,type);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meeting_info_fragment, container, false);

        RecyclerView recyclerView  = view.findViewById(R.id.meeting_participants_recyclerView);
        WhoComingAdapter adapter = new WhoComingAdapter(requireActivity(),mViewModel.getUsers().getValue());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        mapView = view.findViewById(R.id.mapViewMeetingInfo);

        initGoogleMap(savedInstanceState);



        mViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                adapter.notifyDataSetChanged();
                for (User u:users) {
                    if (u.getId().equals(CurrentUser.getInstance().getId())){
                        isUserAlreadyIn = true;
                        toggleIsUserIn();
                        return;
                    }
                }
                isUserAlreadyIn = false;
            }
        });


        if (type.equals(Const.MEETING_TYPE_PUBLIC)){
            mViewModel.getPublicM().observe(getViewLifecycleOwner(), new Observer<Meeting>() {
                @Override
                public void onChanged(Meeting meeting) {
                    updateUI(meeting);
                }
            });
        }else if(type.equals(Const.MEETING_TYPE_GROUP)){
            mViewModel.getGroupM().observe(getViewLifecycleOwner(), new Observer<GroupMeeting>() {
                @Override
                public void onChanged(GroupMeeting groupMeeting) {
                    updateUI(groupMeeting);
                }
            });
        }

        view.findViewById(R.id.im_coming_button_meeting_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserAlreadyIn){
                    if (type.equals(Const.MEETING_TYPE_PUBLIC)) {
                        Meeting meeting = mViewModel.getPublicM().getValue();
                        CurrentUser.quitMeeting(meeting.getId(), FirebaseTags.PUBLIC_MEETINGS_CHILDES);
                        meeting.deleteUserArrival(CurrentUser.getInstance().getId());
                    } else if (type.equals(Const.MEETING_TYPE_GROUP)) {
                        GroupMeeting meeting = mViewModel.getGroupM().getValue();
                        CurrentUser.quitMeeting(meeting.getId(), FirebaseTags.GROUP_MEETINGS_CHILDES);
                        meeting.deleteUserArrival(CurrentUser.getInstance().getId());
                    }
                }else {
                    if (type.equals(Const.MEETING_TYPE_PUBLIC)) {
                        Meeting meeting = mViewModel.getPublicM().getValue();
                        CurrentUser.joinMeeting(meeting.getId(), FirebaseTags.PUBLIC_MEETINGS_CHILDES, meeting.getId());
                        meeting.confirmUserArrival(CurrentUser.getInstance().getId());
                    } else if (type.equals(Const.MEETING_TYPE_GROUP)) {
                        GroupMeeting meeting = mViewModel.getGroupM().getValue();
                        CurrentUser.joinMeeting(meeting.getId(), FirebaseTags.GROUP_MEETINGS_CHILDES, mViewModel.getGroupId());
                        meeting.confirmUserArrival(CurrentUser.getInstance().getId());
                    }
                }
            }
        });

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
                } else {
                    Toast.makeText(requireActivity(), "Map Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateUI(Meeting meeting) {

        TextView day = view.findViewById(R.id.meeting_info_tv_day_of_month_calendar_item);
        TextView dayOfWeek = view.findViewById(R.id.meeting_info_tv_day_of_week_calendar_item);
        TextView month = view.findViewById(R.id.meeting_info_tv_day_calendar_item);
        TextView hour = view.findViewById(R.id.meeting_info_hour_textView);
        TextView location = view.findViewById(R.id.meeting_info_location_textView);
        TextView description = view.findViewById(R.id.meeting_info_desc_textView);
        Date date = new Date(meeting.getMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        day.setText("" + calendar.get(Calendar.DAY_OF_MONTH));
        dayOfWeek.setText(getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
        month.setText(getMonth(calendar.get(Calendar.MONTH)));
        hour.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        description.setText(meeting.getDescription());
        location.setText(getAddress(meeting.getLocation()));
        mMap.addMarker(new MarkerOptions().position(new LatLng(meeting.getLatitude(), meeting.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(meeting.getLatitude(), meeting.getLongitude())));
        mMap.moveCamera(CameraUpdateFactory.zoomIn());
        //mMap.getUiSettings().setAllGesturesEnabled(false);
    }

    private void toggleIsUserIn(){
        Button button =view.findViewById(R.id.im_coming_button_meeting_info);
        if (isUserAlreadyIn)
            button.setText(getString(R.string.user_participaiting));
        else
            button.setText(getString(R.string.confirm_arrival));
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


    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }
}