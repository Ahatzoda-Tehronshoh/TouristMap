package com.tehronshoh.touristmap.mapKit

import android.content.Context
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapKitUtil(
    private val mapView: MapView
) {

    fun initialize(
        context: Context,
        cameraPosition: CameraPosition,
        animation: Animation
    ) {
        MapKitFactory.initialize(context)
        mapView.map.move(
            cameraPosition,
            animation,
            null
        )
    }

    fun start() {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    fun stop() {
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }

}