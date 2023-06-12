package com.tehronshoh.touristmap.ui.screens.home

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
import com.tehronshoh.touristmap.utils.MapKitUtil
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider

private val listOfPlaces = listOf(
    Place("Парк Рудаки", 38.576290022103, 68.7847677535899, ""),
    Place("Дворец Нации", 38.57625532932752, 68.77876043971129, ""),
    Place("Парк национального флага", 38.58121398293717, 68.78074208988657, ""),
    Place("Национальный музей Таджикистана", 38.582388153207894, 68.77991596953152, ""),
    Place("Филиал МГУ им. М.В. Ломоносова", 38.57935204500182, 68.79011909252935, "")
)

@Composable
fun MapScreen() {
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
    val isUserLocationEnable = true

    val listener = MapObjectTapListener { _, point ->
        Log.d("TAG_MAP", "onCreate: Tapped!")
        choosingPlace = Place("", point.latitude, point.longitude, "")
        showBottomSheet = true

        true
    }

    val point = Point(
        /*currentPosition?.latitude ?: */38.57935204500182,
        /*currentPosition?.longitude ?: */68.79011909252935
    )

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
                        initialize(
                            context = context,
                            cameraPosition = CameraPosition(point, 8f, 0f, 0f),
                            animation = Animation(Animation.Type.SMOOTH, 0.3f),
                            zoomInButton = zoomInButton,
                            zoomOutButton = zoomOutButton
                        )

                        addPlaces(
                            listOfPlaces.map { place ->
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

                        if (isUserLocationEnable)
                            changeUserLocationVisibility()
                    }

                    trafficVisibility.setOnClickListener {
                        mapKitUtil?.changeTrafficVisibility()
                    }

                    locationVisibility.setOnClickListener {
                        if (isUserLocationEnable)
                            mapKitUtil?.changeUserLocationVisibility()
                    }

                    mapKitUtil?.start()
                    onDispose {
                        Log.d("TAG_MAP", "MapScreen: MapKit Stopped!")
                        mapKitUtil?.stop()
                    }
                }

                mapKitUtil?.let {
                    PlaceModalBottomSheetInitialize(
                        showBottomSheet = showBottomSheet,
                        choosingPlace = choosingPlace,
                        currentPosition = point,
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

    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.Expanded },
        //skipHalfExpanded = true
    )

    val coroutineScope = rememberCoroutineScope()

    if (showBottomSheet) {
        Log.d("TAG_TEST", "onCreate: $choosingPlace")
        choosingPlace?.let {
            PlaceBottomSheet(place = it,
                sheetState = sheetState,
                coroutineScope = coroutineScope,
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
    MapScreen()
}