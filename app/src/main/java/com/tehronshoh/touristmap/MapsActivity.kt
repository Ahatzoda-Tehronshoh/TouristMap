package com.tehronshoh.touristmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tehronshoh.touristmap.BuildConfig.MAPS_API_KEY
import com.tehronshoh.touristmap.databinding.ActivityMapsBinding
import com.tehronshoh.touristmap.extensions.animateWithZeroingZoom
import com.tehronshoh.touristmap.extensions.hideBottomSheetPlace
import com.tehronshoh.touristmap.extensions.showBottomSheetPlace
import com.tehronshoh.touristmap.mapKit.MapKitUtil
import com.tehronshoh.touristmap.models.Place
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.random.Random

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    OnMyLocationClickListener, GoogleMap.OnMarkerClickListener, OnRequestPermissionsResultCallback {

    private val mapKitUtil: MapKitUtil by lazy {
        MapKitUtil(binding.yandexMapView)
    }

    private var showBottomSheet = mutableStateOf(false)
    private var currentPosition: LatLng? = null
    private var choosingPlace: Place? = null

    private val listOfPlaces = listOf(
        Place("Парк Рудаки", 38.576290022103, 68.7847677535899, ""),
        Place("Дворец Нации", 38.57625532932752, 68.77876043971129, ""),
        Place("Парк национального флага", 38.58121398293717, 68.78074208988657, ""),
        Place("Национальный музей Таджикистана", 38.582388153207894, 68.77991596953152, ""),
        Place("Филиал МГУ им. М.В. Ломоносова", 38.57935204500182, 68.79011909252935, "")
    )

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeView.setContent {
            PlaceModalBottomSheetInitialize()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val point = Point(
            currentPosition?.latitude ?: 38.57935204500182,
            currentPosition?.longitude ?: 68.79011909252935
        )
        mapKitUtil.apply {
            initialize(
                context = this@MapsActivity,
                cameraPosition = CameraPosition(point, 8f, 0f, 0f),
                animation = Animation(Animation.Type.SMOOTH, 1f),
                zoomInButton = binding.zoomInButton,
                zoomOutButton = binding.zoomOutButton
            )

            val listener = MapObjectTapListener { mapObject, point ->
                Log.d("TAG_MAP", "onCreate: Tapped!")
                choosingPlace =
                    Place("", point.latitude, point.longitude, "")
                showBottomSheet.value = true
                true
            }
            listOfPlaces.forEach {
                addPlace(
                    point = Point(it.latitude, it.longitude),
                    listener = listener
                )
            }
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
            sheetState.showBottomSheetPlace(coroutineScope)
            Log.d("TAG_TEST", "onCreate: $choosingPlace")
            choosingPlace?.let {
                PlaceBottomSheet(place = it,
                    sheetState = sheetState,
                    coroutineScope = coroutineScope,
                    buildRoute = {
                        showBottomSheet.value = false
                        createRouteToDestination(
                            to = LatLng(it.latitude, it.longitude)
                        )
                    }) {
                    showBottomSheet.value = false
                }
            }
        } else
            sheetState.hideBottomSheetPlace(coroutineScope)

        LaunchedEffect(key1 = sheetState.currentValue) {
            if (showBottomSheet.value && (sheetState.currentValue == ModalBottomSheetValue.Hidden)) {
                showBottomSheet.value = false
            }
        }
    }

    interface TestRequest {
        @GET("json")
        fun buildRoute(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("key") key: String = MAPS_API_KEY
        ): Call<ResponseBody>
    }

    // Создание маршрута от текущего местоположения до заданного места
    private fun createRouteToDestination(to: LatLng) {
        currentPosition?.let {
            val origin = "${it.latitude},${it.longitude}"
            val destination = "${to.latitude},${to.longitude}"

            val retrofit =
                Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/api/directions/")
                    .addConverterFactory(GsonConverterFactory.create()).build()

            val testRequest = retrofit.create(TestRequest::class.java)

            testRequest.buildRoute(origin, destination).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            Log.d("TAG_MAP", "onResponse: ${response.body()!!.string()}")
                            Toast.makeText(
                                this@MapsActivity,
                                response.body()!!.string(),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else
                            Log.d("TAG_MAP", "onResponse: null")
                    } else
                        Log.d("TAG_MAP", "onResponse: ${response.message()}")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("TAG_MAP", "onFailure: ${t.message}")
                    Toast.makeText(this@MapsActivity, t.message, Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener(this)

        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
        }


        mMap.apply {
            setOnMyLocationButtonClickListener(this@MapsActivity)
            setOnMyLocationClickListener(this@MapsActivity)
            animateCamera(CameraUpdateFactory.zoomTo(0F))
        }
        enableMyLocation()

        // Add a marker in Dushanbe and move and zoom the camera
        addPlacesInMap()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Toast.makeText(this, marker.title, Toast.LENGTH_SHORT).show()
        choosingPlace =
            Place(marker.title.toString(), marker.position.latitude, marker.position.longitude, "")
        showBottomSheet.value = true
        return true
    }

    private fun addPlacesInMap() {
        for (place in listOfPlaces) mMap.addMarker(
            MarkerOptions().position(
                LatLng(place.latitude, place.longitude)
            ).title(place.name)
        )
    }

    fun randomAnimateWithZeroingZoom() {
        lifecycleScope.launch(Dispatchers.IO) {
            val callback = object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    // Обратный вызов, когда анимация завершена
                }

                override fun onCancel() {
                    // Обратный вызов, если анимация была отменена
                }
            }
            var to = LatLng(38.53575, 68.77905)
            var from: LatLng

            repeat(5) {
                delay(15000)
                from = to
                to = LatLng(Random.nextDouble(-10.0, 90.0), Random.nextDouble(-180.0, 180.0))

                withContext(Dispatchers.Main) {
                    mMap.animateWithZeroingZoom(
                        from, to, 3000, 5000, 3000, callback
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // Проверка доступности провайдера местоположения
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // Провайдер местоположения недоступен, обработка этой ситуации
                return
            }

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener
            )

            return
        }

        // 2. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private val locationListener: LocationListener = LocationListener { location ->
        currentPosition = LatLng(location.latitude, location.longitude)
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG).show()
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
                if (permissions[0] == Manifest.permission.ACCESS_COARSE_LOCATION) {
                    enableMyLocation()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapKitUtil.start()
    }

    override fun onStop() {
        super.onStop()
        mapKitUtil.stop()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}