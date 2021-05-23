package com.example.meetapp.callbacks;

import com.google.android.gms.maps.model.LatLng;

public interface OnDismissPlacePicker {
    /**
     * a method that sends LatLng after a location is selected
     * @param latLng represent a location
     */
    void getSelectedLocation(LatLng latLng);
}
