package com.tehronshoh.touristmap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.tehronshoh.touristmap.databinding.ActivityMapsBinding
import com.tehronshoh.touristmap.extensions.hideBottomSheetPlace
import com.tehronshoh.touristmap.extensions.showBottomSheetPlace
import com.tehronshoh.touristmap.mapKit.MapKitUtil
import com.tehronshoh.touristmap.models.Place
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.image.ImageProvider

class MapsActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {

    private val mapKitUtil: MapKitUtil by lazy {
        MapKitUtil(binding.yandexMapView)
    }

    private var showBottomSheet = mutableStateOf(false)
    private var currentPosition: Point? = null
    private var choosingPlace: Place? = null

    private val listOfPlaces = listOf(
        Place("Парк Рудаки", 38.576290022103, 68.7847677535899, ""),
        Place("Дворец Нации", 38.57625532932752, 68.77876043971129, ""),
        Place("Парк национального флага", 38.58121398293717, 68.78074208988657, ""),
        Place("Национальный музей Таджикистана", 38.582388153207894, 68.77991596953152, ""),
        Place("Филиал МГУ им. М.В. Ломоносова", 38.57935204500182, 68.79011909252935, "")
    )

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyLocationManager()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeView.setContent {
            PlaceModalBottomSheetInitialize()
        }

        val point = Point(
            currentPosition?.latitude ?: 38.57935204500182,
            currentPosition?.longitude ?: 68.79011909252935
        )
        mapKitUtil.apply {
            initialize(
                context = this@MapsActivity,
                cameraPosition = CameraPosition(point, 8f, 0f, 0f),
                animation = Animation(Animation.Type.SMOOTH, 0.3f),
                zoomInButton = binding.zoomInButton,
                zoomOutButton = binding.zoomOutButton
            )

            addPlaces(
                listOfPlaces.map { place ->
                    Point(
                        place.latitude,
                        place.longitude
                    )
                },
                ImageProvider.fromResource(this@MapsActivity, R.drawable.location_icon_48),
                listener
            )

            if (enableMyLocation())
                changeUserLocationVisibility()
        }

        setListeners()

    }

    private val listener = MapObjectTapListener { _, point ->
        Log.d("TAG_MAP", "onCreate: Tapped!")
        choosingPlace =
            Place("", point.latitude, point.longitude, "")
        showBottomSheet.value = true

        true
    }

    private fun applyLocationManager() {
        if (enableMyLocation()) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Проверка доступности провайдера местоположения
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Провайдер местоположения недоступен, обработка этой ситуации
                return
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener
            )
        }
    }

    private fun setListeners() = binding.apply {
        trafficVisibility.setOnClickListener {
            mapKitUtil.changeTrafficVisibility()
        }

        locationVisibility.setOnClickListener {
            if (enableMyLocation())
                mapKitUtil.changeUserLocationVisibility()
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun PlaceModalBottomSheetInitialize() {
        val showBottomSheetRemember by remember {
            showBottomSheet
        }
        val sheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmValueChange = { it != ModalBottomSheetValue.Expanded },
            //skipHalfExpanded = true
        )

        val coroutineScope = rememberCoroutineScope()

        if (showBottomSheetRemember) {
            binding.composeView.visibility = VISIBLE
            Log.d("TAG_TEST", "onCreate: $choosingPlace")
            choosingPlace?.let {
                PlaceBottomSheet(place = it,
                    sheetState = sheetState,
                    coroutineScope = coroutineScope,
                    buildRoute = {
                        if (currentPosition != null) {
                            showBottomSheet.value = false
                            mapKitUtil.submitRequestForDrawingRoute(
                                routeStartLocation = Point(
                                    currentPosition!!.latitude,
                                    currentPosition!!.longitude
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
                                this@MapsActivity,
                                "Ваше местоположения неизвестно, повторите еще раз!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                    showBottomSheet.value = false
                }
            }

            sheetState.showBottomSheetPlace(coroutineScope)
        } else {
            sheetState.hideBottomSheetPlace(coroutineScope)
            binding.composeView.visibility = GONE
        }

        LaunchedEffect(key1 = sheetState.currentValue) {
            if (showBottomSheet.value && (sheetState.currentValue == ModalBottomSheetValue.Hidden)) {
                showBottomSheet.value = false
            }
        }
    }


    private fun enableMyLocation(): Boolean {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
            return true

        Toast.makeText(
            this,
            "Пожалуйста, дайте разришения!",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("TAG_MAP", "enableMyLocation: Permissions are not granted!")
        // 2. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )

        return false
    }

    private val locationListener: LocationListener = LocationListener { location ->
        currentPosition = Point(location.latitude, location.longitude)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
            )
            return
        }
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (permissions[0] in listOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    applyLocationManager()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapKitUtil.start()
    }

    override fun onStop() {
        mapKitUtil.stop()
        super.onStop()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}