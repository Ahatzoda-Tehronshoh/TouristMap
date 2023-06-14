package com.tehronshoh.touristmap.ui.screens.home

import android.os.Parcelable
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.tehronshoh.touristmap.R
import com.tehronshoh.touristmap.databinding.MapViewBinding
import com.tehronshoh.touristmap.extensions.getBitmapFromVectorDrawable
import com.tehronshoh.touristmap.extensions.hideBottomSheetPlace
import com.tehronshoh.touristmap.extensions.showBottomSheetPlace
import com.tehronshoh.touristmap.model.Place
import com.tehronshoh.touristmap.ui.components.PlaceBottomSheet
import com.tehronshoh.touristmap.ui.tool.LocalStaticPlaces
import com.tehronshoh.touristmap.ui.tool.LocalUserCurrentPosition
import com.tehronshoh.touristmap.utils.MapKitUtil
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider
import kotlinx.parcelize.Parcelize
import kotlin.math.abs

@Parcelize
data class MapKitConfigure(
    val pointLatitude: Double,
    val pointLongitude: Double,
    val currentZoom: Float,
    val azimuth: Float = 0f,
    val tilt: Float = 0f
) : Parcelable

@Composable
fun MapScreen(mapKitConfigure: MapKitConfigure, onConfigureChange: (MapKitConfigure) -> Unit) {
    val listOfPlace = LocalStaticPlaces.current
    val currentPosition = LocalUserCurrentPosition.current

    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    var choosingPlace by remember {
        mutableStateOf<Place?>(null)
    }

    var mapKitUtil by remember {
        mutableStateOf<MapKitUtil?>(null)
    }

    val context = LocalContext.current

    val listener = MapObjectTapListener { _, p ->
        Log.d("TAG_MAP", "onCreate: Tapped!")

        listOfPlace.firstOrNull { (abs(it.latitude - p.latitude) < 0.0001 && abs(it.longitude - p.longitude) < 0.0001) }?.let {
            Log.d("TAG_MAP", "onCreate: ${p.latitude} - ${p.longitude}!")
            Log.d("TAG_MAP", "onCreate: ${it.latitude} - ${it.longitude}!")
            choosingPlace = it
            showBottomSheet = true
        }

        true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AndroidViewBinding(factory = MapViewBinding::inflate) {
            mapKitUtil = MapKitUtil(yandexMapView)

            composeView.setContent {
                if (showBottomSheet)
                    composeView.visibility = VISIBLE
                else
                    composeView.visibility = GONE

                DisposableEffect(Unit) {
                    Log.d("TAG_MAP", "MapScreen: MapKit Started!")

                    mapKitUtil?.apply {
                        Log.d("TAG_MAP", "MapScreen: $mapKitConfigure")
                        initialize(
                            context = context,
                            cameraPosition = CameraPosition(
                                Point(
                                    mapKitConfigure.pointLatitude,
                                    mapKitConfigure.pointLongitude
                                ),
                                mapKitConfigure.currentZoom,
                                mapKitConfigure.azimuth,
                                mapKitConfigure.tilt
                            ),
                            animation = Animation(Animation.Type.SMOOTH, 0.3f),
                            zoomInButton = zoomInButton,
                            zoomOutButton = zoomOutButton
                        )

                        addPlaces(
                            listOfPlace.map { place ->
                                Point(
                                    place.latitude,
                                    place.longitude
                                )
                            },
                            ImageProvider.fromBitmap(
                                context.getBitmapFromVectorDrawable(R.drawable.baseline_location_on_24)
                            ),
                            listener
                        )

                        if (currentPosition != null)
                            changeUserLocationVisibility()
                    }

                    trafficVisibility.setOnClickListener {
                        mapKitUtil?.changeTrafficVisibility()
                    }

                    locationVisibility.setOnClickListener {
                        if (currentPosition != null)
                            mapKitUtil?.changeUserLocationVisibility()
                    }

                    mapKitUtil?.start()
                    onDispose {
                        Log.d("TAG_MAP", "MapScreen: MapKit Stopped!")
                        onConfigureChange(
                            MapKitConfigure(
                                pointLatitude = yandexMapView.map.cameraPosition.target.latitude,
                                pointLongitude = yandexMapView.map.cameraPosition.target.longitude,
                                currentZoom = yandexMapView.map.cameraPosition.zoom,
                                azimuth = yandexMapView.map.cameraPosition.azimuth,
                                tilt = yandexMapView.map.cameraPosition.tilt
                            )
                        )
                        mapKitUtil?.stop()
                    }
                }

                mapKitUtil?.let {
                    PlaceModalBottomSheetInitialize(
                        showBottomSheet = showBottomSheet,
                        choosingPlace = choosingPlace,
                        currentPosition = currentPosition,
                        mapKitUtil = it,
                        onBottomStateChange = { b ->
                            showBottomSheet = b
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PlaceModalBottomSheetInitialize(
    showBottomSheet: Boolean,
    choosingPlace: Place?,
    currentPosition: Point?,
    mapKitUtil: MapKitUtil,
    onBottomStateChange: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val coroutineScope = rememberCoroutineScope()

    if (showBottomSheet) {
        Log.d("TAG_TEST", "onCreate: $choosingPlace")
        choosingPlace?.let {
            PlaceBottomSheet(place = it,
                sheetState = sheetState,
                buildRoute = {
                    if (currentPosition != null) {
                        onBottomStateChange(false)
                        mapKitUtil.submitRequestForDrawingRoute(
                            routeStartLocation = Point(
                                currentPosition.latitude,
                                currentPosition.longitude
                            ),
                            routeEndLocation = Point(
                                it.latitude,
                                it.longitude
                            )
                        )
                    } else {
                        Log.d(
                            "TAG_MAP",
                            "PlaceModalBottomSheetInitialize: currentPosition = null"
                        )
                        Toast.makeText(
                            context,
                            "Ваше местоположения неизвестно, повторите еще раз!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                onBottomStateChange(false)
            }
        }
        sheetState.showBottomSheetPlace(coroutineScope)
    } else {
        sheetState.hideBottomSheetPlace(coroutineScope)
    }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (showBottomSheet && (sheetState.currentValue == ModalBottomSheetValue.Hidden)) {
            onBottomStateChange(false)
        }
    }
}


@Preview
@Composable
private fun MapScreenPreview() {
    //MapScreen()
}