package com.example.meetapp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.meetapp.R;
import com.example.meetapp.databinding.HomeFragmentBinding;
import com.example.meetapp.model.Const;
import com.example.meetapp.model.meetings.Meeting;
import com.example.meetapp.ui.meetings.MeetingsInfoDialog;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    public static final int BITMAP_SIZE = 80;
    private static final int REQUEST_LOCATION = 103;

    private final HashMap<String,String> markersHash = new HashMap<>();
    private final ArrayList<MarkerOptions> markers = new ArrayList<>();
    private final ArrayList<String> ids = new ArrayList<>();

    private HomeViewModel mViewModel;

    private TextView locationTV ,topicTV;
    private CircleImageView topicIV;
    private MapView mapView;
    private GoogleMap mMap;
    private View view;
    private HomeFragmentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         view = binding.getRoot();


        this.mapView = binding.mapView;
        initGoogleMap(savedInstanceState);
        locationTV = binding.locationTextView;
        topicTV = binding.locationSubjectTextView;
        topicIV = binding.homeLocationSubjectCircleImageView;
        EditText locationName = binding.homeEditTextAll;

        binding.homeSearchIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = getLocationFromAddress(locationName.getText().toString());
                zoomToLocation(latLng);
            }
        });

        view.findViewById(R.id.constraintLayout4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingsInfoDialog meetingsInfoDialog = new MeetingsInfoDialog();
                int i = mViewModel.getPublicHash().get((String)v.getTag());
                Meeting m = mViewModel.getMeetings().getValue().get(i).getValue();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Const.BUNDLE_MEETING,m);
                meetingsInfoDialog.setArguments(bundle);
                meetingsInfoDialog.show(getFragmentManager(),"Meeting Dialog");
            }
        });

        mViewModel.getMeetings().observe(getViewLifecycleOwner(), new Observer<ArrayList<MutableLiveData<Meeting>>>() {
            @Override
            public void onChanged(ArrayList<MutableLiveData<Meeting>> mutableLiveData) {
                for (MutableLiveData<Meeting> m: mutableLiveData) {
                    if (!m.hasObservers()) {
                        m.observe(getViewLifecycleOwner(), new Observer<Meeting>() {
                            @Override
                            public void onChanged(Meeting meeting) {
                                if (!markersHash.containsKey(meeting.getId())) {
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.title(meeting.getSubject());
                                    markerOptions.position(meeting.getLocation());
                                    Bitmap icon = BitmapFactory.decodeResource(requireContext().getResources(),getSubjectIcon(meeting.getSubject()));
                                    icon = Bitmap.createScaledBitmap(icon, BITMAP_SIZE, BITMAP_SIZE, false);
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
                                    ids.add(meeting.getId());
                                    markers.add(markerOptions);
                                    markersHash.put(meeting.getId(),meeting.getId());
                                }
                            }
                        });
                    }
                    if (mapView!=null)
                        addMarkers();
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    private LatLng getLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            assert locationManager != null;
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double latitude = locationGPS.getLatitude();
                double longitude = locationGPS.getLongitude();
                return new LatLng(latitude,longitude);
            }
        }
        return new LatLng(0,0);
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        LatLng latLng = getLocation();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);

                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                             ((MotionLayout)view.findViewById(R.id.home_motion_layout)).transitionToStart();
                        }
                    });
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            locationTV.setText(getAddress(marker.getPosition()));
                            topicTV.setText(marker.getTitle());
                            topicIV.setImageResource(getSubjectIcon(marker.getTitle()));
                            ((MotionLayout)view.findViewById(R.id.home_motion_layout)).transitionToEnd();
                            view.findViewById(R.id.constraintLayout4).setTag(marker.getTag());
                            return false;
                        }
                    });
                    addMarkers();
                } else {
                    Toast.makeText(requireActivity(), "Map Error", Toast.LENGTH_LONG).show();
                }
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
                p1 = new LatLng(location.getLatitude(), location.getLongitude() );
            }
        } catch (IOException ex) {

            ex.printStackTrace();
        }
        return p1;
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

    private void addMarkerToMap(LatLng location) {
        if (location != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            markerOptions.title(location.latitude + " : " + location.longitude);
            mMap.addMarker(markerOptions);
        }
    }

    private void addMarkerToMap(MarkerOptions location , String id) {
        if (location != null && mMap!= null) {
            Marker m = mMap.addMarker(location);
            m.setTag(id);
        }
    }
    private void addMarkers(){
        for (int i = 0; i < markers.size(); i++) {
            addMarkerToMap(markers.get(i) , ids.get(i));
        }
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

    public void fixGoogleBug(){
        SharedPreferences googleBug = requireActivity().getSharedPreferences("google_bug", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(requireActivity().getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }
    }

    private void zoomToLocation(LatLng location){
        if (location!=null) {
            CameraUpdate center = CameraUpdateFactory.newLatLng(location);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(20);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
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