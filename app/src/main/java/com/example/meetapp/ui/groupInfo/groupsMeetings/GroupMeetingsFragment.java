package com.example.meetapp.ui.groupInfo.groupsMeetings;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.meetapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter;
import com.michalsvec.singlerowcalendar.utils.DateUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kotlin.jvm.internal.markers.KMutableList;

public class GroupMeetingsFragment extends Fragment {

    private GroupMeetingsViewModel mViewModel;

    private MapView mapView;
    private GoogleMap mMap;
    private double latitude = 0;
    private double longitude = 0;
    Calendar calendar = Calendar.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_meetings_fragment, container, false);
        this.mapView = view.findViewById(R.id.group_meetings_mapView);

        initGoogleMap(savedInstanceState);

        calendar.setTime(new Date());
        int currentMonth = calendar.MONTH;


        CalendarViewManager calendarViewManager = new CalendarViewManager() {
            @Override
            public int setCalendarViewResourceId(int i, @NotNull Date date, boolean isSelected) {
                int layout = 0;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if (isSelected){
                    return R.layout.speical_selected_calender_item;
                }else {
                    return R.layout.speical_calender_item;
                }
            }

            @Override
            public void bindDataToCalendarView(@NotNull SingleRowCalendarAdapter.CalendarViewHolder holder, @NotNull Date date, int i, boolean b) {
                ((TextView)holder.itemView.findViewById(R.id.tv_date_calendar_item)).setText(DateUtils.INSTANCE.getDayName(date));
                ((TextView)holder.itemView.findViewById(R.id.tv_day_calendar_item)).setText(DateUtils.INSTANCE.getDay3LettersName(date));

            }
        };

        SingleRowCalendar singleRowCalendar  = view.findViewById(R.id.singleRowCalendar);
        singleRowCalendar.setCalendarViewManager(calendarViewManager);
    //    singleRowCalendar.setDates(getFutureDatesOfCurrentMonth());

        return view;
    }

//    private List<? extends Date> getFutureDatesOfCurrentMonth() {
////        currentMonth++ // + because we want next month
////        if (currentMonth == 12) {
////            // we will switch to january of next year, when we reach last month of year
////            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
////            currentMonth = 0 // 0 == january
////        }
////        return getDates(mutableListOf())
//    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.setBuildingsEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
                    //seekBar.setProgress((int) (mMap.getCameraPosition().zoom*5));
                } else {
                    Toast.makeText(getActivity(), "Map Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(GroupMeetingsViewModel.class);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
}