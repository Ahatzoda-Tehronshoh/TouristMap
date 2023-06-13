package com.tehronshoh.touristmap.utils

import android.content.Context
import android.widget.Button
import android.widget.Toast
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

class MapKitUtil(
    private val mapView: MapView
) : DrivingSession.DrivingRouteListener {

    private val mapKit: MapKit by lazy {
        MapKitFactory.getInstance()
    }

    private val trafficLayer by lazy {
        mapKit.createTrafficLayer(mapView.mapWindow)
    }

    private val userLocationLayer by lazy {
        mapKit.createUserLocationLayer(mapView.mapWindow)
    }

    private val mapObjects: MapObjectCollection by lazy {
        mapView.map.mapObjects.addCollection()
    }

    private val drivingRouter: DrivingRouter by lazy {
        DirectionsFactory.getInstance().createDrivingRouter()
    }

    private var drivingSession: DrivingSession? = null

    private val currentZoom
        get() = mapView.map.cameraPosition.zoom

    fun initialize(
        context: Context,
        cameraPosition: CameraPosition,
        animation: Animation,
        zoomInButton: Button,
        zoomOutButton: Button
    ) {
        MapKitFactory.initialize(context)
        mapView.map.move(
            cameraPosition, animation, null
        )
        zoomInButton.setOnClickListener {
            mapView.map.move(
                CameraPosition(
                    mapView.map.cameraPosition.target,
                    currentZoom + 1.0f,
                    mapView.map.cameraPosition.azimuth,
                    mapView.map.cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 0.3f), null
            )
        }

        zoomOutButton.setOnClickListener {
            mapView.map.move(
                CameraPosition(
                    mapView.map.cameraPosition.target,
                    currentZoom - 1.0f,
                    mapView.map.cameraPosition.azimuth,
                    mapView.map.cameraPosition.tilt
                ), Animation(Animation.Type.SMOOTH, 0.3f), null
            )
        }
        mapView.map.isZoomGesturesEnabled = true
        mapView.map.isRotateGesturesEnabled = false
    }

    fun submitRequestForDrawingRoute(
        routeStartLocation: Point,
        routeEndLocation: Point,
        requestPointType: RequestPointType = RequestPointType.WAYPOINT
    ) {
        cancelRoute()

        val screenCenterLocation = Point(
            (routeStartLocation.latitude + routeEndLocation.latitude) / 2,
            (routeStartLocation.longitude + routeEndLocation.longitude) / 2
        )

        mapView.map.move(
            CameraPosition(
                screenCenterLocation, currentZoom, 0f, 0f
            )
        )

        drivingSession = drivingRouter.requestRoutes(
            listOf(
                RequestPoint(
                    routeStartLocation, requestPointType, null
                ),
                RequestPoint(
                    routeEndLocation, requestPointType, null
                )
            ), DrivingOptions(), VehicleOptions(), this
        )
    }

    private fun cancelRoute() {
        drivingSession?.cancel()
    }

    fun start() {
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    fun stop() {
        MapKitFactory.getInstance().onStop()
        mapView.onStop()
    }

    fun addPlaces(
        points: List<Point>, imageProvider: ImageProvider, listener: MapObjectTapListener
    ) {
        mapObjects.addTapListener(listener)
        mapObjects.addPlacemarks(points, imageProvider, IconStyle())
    }

    fun changeTrafficVisibility() {
        trafficLayer.isTrafficVisible = !trafficLayer.isTrafficVisible
    }

    fun changeUserLocationVisibility() {
        userLocationLayer.isVisible = !userLocationLayer.isVisible
    }

    override fun onDrivingRoutes(routes: MutableList<DrivingRoute>) {
        for (route in routes) mapObjects.addPolyline(route.geometry)
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage: String? = "getString(R.string.unknown_error_message)"
        if (error is RemoteError) errorMessage = "getString(R.string.remote_error_message)"
        else if (error is NetworkError) errorMessage = "getString(R.string.network_error_message)"

        Toast.makeText(mapView.context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}