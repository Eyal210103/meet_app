package com.example.meetapp.callbacks;

import com.google.android.gms.maps.model.LatLng;

public interface OnDismissPlacePicker {
    void getSelectedLocation(LatLng latLng);
}
