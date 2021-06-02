package com.example.meetapp.ui.createMeeting;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.meetapp.R;
import com.example.meetapp.callbacks.OnDismissPlacePicker;
import com.example.meetapp.databinding.FragmentPlacePickerBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlacePickerDialog extends DialogFragment {

    private static final int REQUEST_LOCATION = 103;
    private MapView mapView;
    private GoogleMap mMap;
    LatLng location;
    Fragment context;
    FragmentPlacePickerBinding binding;
    TextView textView;

    public PlacePickerDialog(Fragment context) {
        this.context = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlacePickerBinding.inflate(inflater);
        View view= binding.getRoot();
        this.mapView = binding.mapViewPlacerPicker;
        EditText editText = binding.placePickerEditTextAll;
        textView = binding.placePickerSelectedTextView;
        binding.placePickerSearchIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().matches("")) {
                    LatLng latLng = getLocationFromAddress(editText.getText().toString());
                    addMarkerToMap(latLng);
                    zoomToLocation(latLng);
                }
            }
        });
        initGoogleMap(savedInstanceState);

        view.findViewById(R.id.place_picker_ok_textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location!= null) {
                    OnDismissPlacePicker onDismissPlacePicker = (OnDismissPlacePicker) context;
                    onDismissPlacePicker.getSelectedLocation(location);
                }
                PlacePickerDialog.this.dismiss();
            }
        });

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
                    public void onMarkerDragStart(Marker arg0) {}

                    @Override
                    public void onMarkerDragEnd(Marker arg0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                        location = arg0.getPosition();
                        updateUI();
                    }

                    @Override
                    public void onMarkerDrag(Marker arg0) {
                    }
                });
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        location = marker.getPosition();
                        updateUI();
                        return true;
                    }
                });
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

    private void addMarkerToMap(LatLng location) {
        if (location != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            markerOptions.title("Selected Location");
            mMap.addMarker(markerOptions);
            markerOptions.draggable(true);
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

    private void updateUI(){
        textView.setText(getAddress(location));
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