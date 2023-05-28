package com.tehronshoh.touristmap

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

fun GoogleMap.animateWithZeroingZoom(
    fromLatLng: LatLng,
    toLatLng: LatLng,
    durationZeroingZoom: Int,
    durationMovingCamera: Int,
    durationZooming: Int,
    callback: GoogleMap.CancelableCallback
) {
    animateCamera(
        CameraUpdateFactory.newLatLngZoom(fromLatLng, 0f),
        durationZeroingZoom,
        callback
    )
    animateCamera(
        CameraUpdateFactory.newLatLng(toLatLng),
        durationMovingCamera,
        callback
    )
    animateCamera(
        CameraUpdateFactory.newLatLngZoom(toLatLng, 8f),
        durationZooming,
        callback
    )
}