package com.tehronshoh.touristmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.maps.model.MarkerOptions
import com.tehronshoh.touristmap.databinding.ActivityMapsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    OnMyLocationButtonClickListener,
    OnMyLocationClickListener,
    OnRequestPermissionsResultCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

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
        val dushanbe = LatLng(38.53575, 68.77905)
        mMap.addMarker(MarkerOptions().position(dushanbe).title("Marker in Dushanbe"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dushanbe, 10f))

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
                to =
                    LatLng(Random.nextDouble(-10.0, 90.0), Random.nextDouble(-180.0, 180.0))

                withContext(Dispatchers.Main) {
                    mMap.animateWithZeroingZoom(
                        from,
                        to,
                        3000,
                        5000,
                        3000,
                        callback
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            return
        }

        // 2. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
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

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}