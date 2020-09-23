package com.example.meetapp.ui.groupInfo.groupsMeetings;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetapp.R;
import com.example.meetapp.ui.Views.CalenderBarPackage.CalenderBar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Calendar;

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

        CalenderBar calenderBar = new CalenderBar(this,R.layout.speical_calender_item);
        calenderBar.setNextDaysButton(view.findViewById(R.id.group_meetings_arrow_forward_imageView));
        calenderBar.setPreviousDaysButton(view.findViewById(R.id.group_meetings_arrow_back_imageView));
        RecyclerView recyclerView = view.findViewById(R.id.group_meetings_calender_recycler);
        calenderBar.setRecyclerView(recyclerView);
        calenderBar.setSeekBar((SeekBar)view.findViewById(R.id.group_meetings_seekBar));

        return view;
    }

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