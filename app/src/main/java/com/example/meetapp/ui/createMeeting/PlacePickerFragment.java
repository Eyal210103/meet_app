package com.example.meetapp.ui.createMeeting;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.meetapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlacePickerFragment extends Fragment {

    private static final int REQUEST_LOCATION = 103;
    private MapView mapView;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_place_picker, container, false);
        this.mapView = view.findViewById(R.id.mapView_placer_picker);
        initGoogleMap(savedInstanceState);
        return view;
    }

    private LatLng getLocation() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    googleMap.setBuildingsEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(10f));
                } else {
                    Toast.makeText(requireActivity(), "Map Error", Toast.LENGTH_LONG).show();
                }
                mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker arg0) {
                        Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onMarkerDragEnd(Marker arg0) {
                        Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);

                        mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                    }

                    @Override
                    public void onMarkerDrag(Marker arg0) {
                        Log.i("System out", "onMarkerDrag...");
                    }
                });
            }
        });
    }
}