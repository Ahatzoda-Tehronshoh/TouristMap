package com.tehronshoh.touristmap.mapKit

import android.content.Context
import android.widget.Button
import androidx.core.view.ActionProvider
import com.tehronshoh.touristmap.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class MapKitUtil(
    private val mapView: MapView
) {
    fun initialize(
        context: Context,
        cameraPosition: CameraPosition,
        animation: Animation,
        zoomInButton: Button,
        zoomOutButton: Button
    ) {
        MapKitFactory.initialize(context)
        mapView.map.move(
            cameraPosition,
            animation,
            null
        )
        zoomInButton.setOnClickListener {
            val currentZoom = mapView.map.cameraPosition.zoom
            val newZoom = currentZoom + 1.0f
            mapView.map.move(
                CameraPosition(
                    mapView.map.cameraPosition.target,
                    newZoom,
                    mapView.map.cameraPosition.azimuth,
                    mapView.map.cameraPosition.tilt
                ),
                Animation(Animation.Type.SMOOTH, 0.3f),
                null
            )
        }

        zoomOutButton.setOnClickListener {
            val currentZoom = mapView.map.cameraPosition.zoom
            val newZoom = currentZoom - 1.0f
            mapView.map.move(
                CameraPosition(
                    mapView.map.cameraPosition.target,
                    newZoom,
                    mapView.map.cameraPosition.azimuth,
                    mapView.map.cameraPosition.tilt
                ),
                Animation(Animation.Type.SMOOTH, 0.3f),
                null
            )
        }
        mapView.map.isZoomGesturesEnabled = true
        mapView.map.isRotateGesturesEnabled = false
    }

    fun start() {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    fun stop() {
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }

    fun addPlace(
        point: Point,
        imageProvider: ImageProvider = ImageProvider.fromResource(
            mapView.context,
            R.drawable.ic_launcher_background
        ),
        listener: MapObjectTapListener
    ) {
        mapView.map.mapObjects.addPlacemark(point, imageProvider).addTapListener(listener)
    }

    fun addPlaces(
        points: List<Point>,
        imageProvider: ImageProvider = ImageProvider.fromResource(
            mapView.context,
            R.drawable.place_icon
        ),
        listener: MapObjectTapListener
    ) {
        for (point in points)
            addPlace(point, imageProvider, listener)
    }
}