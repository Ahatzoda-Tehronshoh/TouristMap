package com.tehronshoh.touristmap.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.tehronshoh.touristmap.ui.navigation.AppNavGraph
import com.tehronshoh.touristmap.ui.tool.LocalUser
import com.tehronshoh.touristmap.ui.tool.LocalUserCurrentPosition
import com.tehronshoh.touristmap.utils.SharedPreferencesUtil
import com.tehronshoh.touristmap.viewmodel.ActivityViewModel
import com.yandex.mapkit.geometry.Point

class MapsActivity : AppCompatActivity(), OnRequestPermissionsResultCallback {
    private val sharedPreferencesUtil = SharedPreferencesUtil.getInstance(this)
    private var currentPosition = mutableStateOf(
        Point(
            38.57935204500182,
            68.79011909252935
        )
    //38.5804, 68.7291
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyLocationManager()

        setContent {
            AppTheme {
                val activityViewModel: ActivityViewModel = viewModel()

                val navController = rememberNavController()

                val currentPositionRemember = remember(currentPosition) {
                    currentPosition
                }

                val user = activityViewModel.userFlow.collectAsState()

                LaunchedEffect(sharedPreferencesUtil.userId) {
                    activityViewModel.getUserById(sharedPreferencesUtil.userId.value)
                }

                Log.d("TAG_APP", "onCreate: ${user.value}")

                CompositionLocalProvider(LocalUser provides user.value) {
                    CompositionLocalProvider(LocalUserCurrentPosition provides currentPositionRemember.value) {
                        AppNavGraph(
                            fragmentManager = supportFragmentManager,
                            navController = navController
                        )
                    }
                }
            }
        }
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
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
                return
            }

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener
            )
        }
    }


    private fun enableMyLocation(): Boolean {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
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
        currentPosition.value = Point(location.latitude, location.longitude)
        Toast.makeText(
            this,
            ": ${currentPosition.value.latitude} - ${currentPosition.value.longitude}",
            Toast.LENGTH_SHORT
        ).show()
        Log.d(
            "TAG_APP",
            ": ${currentPosition.value.latitude} - ${currentPosition.value.longitude}"
        )
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}