package com.tehronshoh.touristmap.utils

import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.tehronshoh.touristmap.model.RouteType
import com.tehronshoh.touristmap.model.TransportType
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
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.transport.TransportFactory
import com.yandex.mapkit.transport.masstransit.BicycleRouterV2
import com.yandex.mapkit.transport.masstransit.PedestrianRouter
import com.yandex.mapkit.transport.masstransit.Route
import com.yandex.mapkit.transport.masstransit.Session
import com.yandex.mapkit.transport.masstransit.TimeOptions
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError

class MapKitUtil(
    private val mapView: MapView
) : DrivingSession.DrivingRouteListener, Session.RouteListener, LocationListener {

    private val mapKit: MapKit by lazy {
        MapKitFactory.getInstance()
    }

    private val locationManager: LocationManager by lazy {
        MapKitFactory.getInstance().createLocationManager()
    }

    private var lastLocation: Point? = null

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

    private val pedestrianRouter: PedestrianRouter by lazy {
        TransportFactory.getInstance().createPedestrianRouter()
    }

    private val bicycleRouter: BicycleRouterV2 by lazy {
        TransportFactory.getInstance().createBicycleRouterV2()
    }

    private var countOfRoutes = 1

    private var drivingSession: DrivingSession? = null
    private var pedestrianSession: Session? = null
    private var bicycleSession: Session? = null

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
        locationManager.requestSingleUpdate(this)
        locationManager.resume()

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
        listOfPlaces: List<Point>,
        routeType: RouteType,
        routeTraffic: TransportType
    ) {

        val screenCenterLocation = Point(
            (routeStartLocation.latitude + listOfPlaces.last().latitude) / 2,
            (routeStartLocation.longitude + listOfPlaces.last().longitude) / 2
        )

        mapView.map.move(
            CameraPosition(
                screenCenterLocation, currentZoom, 0f, 0f
            )
        )

        val requestPointsList = listOfPlaces.mapIndexed { index, point ->
            if (index == listOfPlaces.lastIndex)
                RequestPoint(
                    point, RequestPointType.WAYPOINT, null
                )
            else
                RequestPoint(
                    point, RequestPointType.VIAPOINT, null
                )
        }.toMutableList()

        requestPointsList.add(
            RequestPoint(
                routeStartLocation, RequestPointType.WAYPOINT, null
            )
        )

        countOfRoutes = if (routeType == RouteType.SINGLE) 1 else 10

        when (routeTraffic) {
            TransportType.WALKING -> {
                drivingSession?.cancel()
                bicycleSession?.cancel()

                pedestrianSession = pedestrianRouter.requestRoutes(
                    requestPointsList,
                    TimeOptions(),
                    this
                )
            }

            TransportType.BICYCLE -> {
                drivingSession?.cancel()
                pedestrianSession?.cancel()

                bicycleSession = bicycleRouter.requestRoutes(
                    requestPointsList,
                    TimeOptions(),

                    this
                )
            }


            TransportType.CAR -> {
                pedestrianSession?.cancel()
                pedestrianSession?.cancel()

                drivingSession = drivingRouter.requestRoutes(
                    requestPointsList.toList(), DrivingOptions().also {
                        if (routeType == RouteType.SINGLE)
                            it.routesCount = 1
                    }, VehicleOptions(), this
                )
            }
        }
    }

    fun getLastUpdatesLocation(): Point? = lastLocation

    fun cancelRoutes() {
        mapView.map.mapObjects.remove(mapObjects)
        pedestrianSession?.cancel()
        bicycleSession?.cancel()
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
        try {
            trafficLayer.isTrafficVisible = !trafficLayer.isTrafficVisible
        } catch (_: Exception) {
        }
    }

    fun changeUserLocationVisibility() {
        try {
            userLocationLayer.isVisible = true
        } catch (_: Exception) {
        }
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

    override fun onMasstransitRoutes(routes: MutableList<Route>) {
        if (countOfRoutes == 1)
            mapObjects.addPolyline(routes[0].geometry)
        else
            for (route in routes) mapObjects.addPolyline(route.geometry)
    }

    override fun onMasstransitRoutesError(error: Error) {
        var errorMessage: String? = "getString(R.string.unknown_error_message)"
        if (error is RemoteError) errorMessage = "getString(R.string.remote_error_message)"
        else if (error is NetworkError) errorMessage = "getString(R.string.network_error_message)"

        Toast.makeText(mapView.context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onLocationUpdated(location: Location) {
        lastLocation = location.position
        Log.d(
            "TAG_MAP",
            "onLocationUpdated: ${lastLocation!!.latitude} - ${lastLocation!!.longitude}"
        )
    }

    override fun onLocationStatusUpdated(locationStatus: LocationStatus) {}
}