package com.example.meetapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.meetapp.R;
import com.example.meetapp.databinding.HomeFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.Meeting;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public static final int BITMAP_SIZE = 90;
    private static final int REQUEST_LOCATION = 103;

    private final HashMap<String, MarkerOptions> markersHash = new HashMap<>();
    private final ArrayList<MarkerOptions> markers = new ArrayList<>();
    private final ArrayList<String> ids = new ArrayList<>();

    private HomeViewModel mViewModel;

    private MapView mapView;
    private GoogleMap mMap;
    private HomeFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        this.mapView = binding.mapView;
        initGoogleMap(savedInstanceState);

        EditText locationName = binding.homeEditTextAll;

        binding.homeSearchIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = getLocationFromAddress(locationName.getText().toString());
                zoomToLocation(latLng);
            }
        });

        mViewModel.getMeetings().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<Meeting>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<Meeting>> mutableLiveData) {
                for (LiveData<Meeting> m : mutableLiveData) {
                    if (!m.hasObservers()) {
                        m.observe(getViewLifecycleOwner(), new Observer<Meeting>() {
                            @Override
                            public void onChanged(Meeting meeting) {
                                String locationInString = getLatLngString(meeting.getLatitude(), meeting.getLongitude());
                                mViewModel.addMeetingToMarker(locationInString, m);
                                if (!markersHash.containsKey(locationInString)) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(meeting.getLocation());
                                    Bitmap icon = BitmapFactory.decodeResource(requireContext().getResources(), getSubjectIcon(meeting.getSubject()));
                                    icon = Bitmap.createScaledBitmap(icon, BITMAP_SIZE, BITMAP_SIZE, false);
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                    markers.add(markerOptions);
                                    ids.add(locationInString);
                                    markersHash.put(locationInString, markerOptions);
                                } else if (mViewModel.getListOfMeetings(locationInString).size() > 1) {
                                    Bitmap icon = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.multi_meeting);
                                    icon = Bitmap.createScaledBitmap(icon, BITMAP_SIZE, BITMAP_SIZE, false);
                                    markersHash.get(locationInString).icon(BitmapDescriptorFactory.fromBitmap(icon));
                                }
                            }
                        });
                    }
                }
                if (mapView != null)
                    addMarkers();
            }
        });

        //TODO BEHAVIOR IS DIFFERENT-
        mViewModel.getUserMeetings().observe(getViewLifecycleOwner(), new Observer<ArrayList<LiveData<Meeting>>>() {
            @Override
            public void onChanged(ArrayList<LiveData<Meeting>> mutableLiveData) {
                for (LiveData<Meeting> m : mutableLiveData) {
                    if (!m.hasObservers()) {
                        m.observe(getViewLifecycleOwner(), new Observer<Meeting>() {
                            @Override
                            public void onChanged(Meeting meeting) {
                                String id = getLatLngString(meeting.getLatitude(), meeting.getLongitude());
                                mViewModel.addMeetingToMarker(id, m);
                                if (!markersHash.containsKey(id)) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(meeting.getLocation());
                                    Bitmap icon = BitmapFactory.decodeResource(requireContext().getResources(), getSubjectIcon(meeting.getSubject()));
                                    icon = Bitmap.createScaledBitmap(icon, BITMAP_SIZE, BITMAP_SIZE, false);
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                    markers.add(markerOptions);
                                    ids.add(id);
                                    markersHash.put(id, markerOptions);
                                } else if (mViewModel.getListOfMeetings(id).size() > 1) {
                                    Bitmap icon = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.multi_meeting);
                                    icon = Bitmap.createScaledBitmap(icon, BITMAP_SIZE, BITMAP_SIZE, false);
                                    markersHash.get(id).icon(BitmapDescriptorFactory.fromBitmap(icon));
                                }
                            }
                        });
                    }
                }
                if (mapView != null)
                    addMarkers();
            }
        });
        return view;
    }

    private LatLng getLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            assert locationManager != null;
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double latitude = locationGPS.getLatitude();
                double longitude = locationGPS.getLongitude();
                return new LatLng(latitude, longitude);
            }
        }
        return new LatLng(0, 0);
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setMapCameraToCurrentLocation();
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        binding.homeMotionLayout.transitionToStart();
                    }
                });
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
//                            locationTV.setText(getAddress(marker.getPosition()));
//                            topicTV.setText(marker.getTitle());
//                            topicIV.setImageResource(getSubjectIcon(marker.getTitle()));
                        LinearLayoutManager llm = new LinearLayoutManager(requireActivity());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        binding.meetingsInLocationRecyclerView.setLayoutManager(llm);
                        binding.meetingsInLocationRecyclerView.setAdapter(new MeetingsToLocationAdapter(HomeFragment.this, mViewModel.getListOfMeetings((String) marker.getTag()), mViewModel.getGroupMeetingToGroupId(), mViewModel.getAllUserMeetingsIds()));
                        binding.homeMotionLayout.transitionToEnd();
                        binding.constraintLayout4.setTag(marker.getTag());
                        return false;
                    }
                });
                addMarkers();

            }
        });
    }

    private LatLng getLocationFromAddress(String strAddress) {
        Geocoder coder = new Geocoder(requireActivity());
        ArrayList<Address> address;
        LatLng p1 = null;
        try {
            address = (ArrayList<Address>) coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            if (!address.isEmpty()) {
                Address location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return p1;
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
        return "Error Has Occurred";
    }

    private void addMarkerToMap(MarkerOptions location, String id) {
        if (location != null && mMap != null) {
            Marker m = mMap.addMarker(location);
            m.setTag(id);
        }
    }

    private void addMarkers() {
        for (int i = 0; i < markers.size(); i++) {
            addMarkerToMap(markers.get(i), ids.get(i));
        }
    }

    private int getSubjectIcon(String subject) {
        switch (subject) {
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

//    public void fixGoogleBug() {
//        SharedPreferences googleBug = requireActivity().getSharedPreferences("google_bug", Context.MODE_PRIVATE);
//        if (!googleBug.contains("fixed")) {
//            File corruptedZoomTables = new File(requireActivity().getFilesDir(), "ZoomTables.data");
//            corruptedZoomTables.delete();
//            googleBug.edit().putBoolean("fixed", true).apply();
//        }
//    }

    private void zoomToLocation(LatLng location) {
        if (location != null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(location);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
    }

    private String getLatLngString(double latitude, double longitude) {
        return "" + latitude + "" + longitude;
    }

    private void setMapCameraToCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }else{
            LatLng latLng = getLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
         //   mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            setMapCameraToCurrentLocation();
        }
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