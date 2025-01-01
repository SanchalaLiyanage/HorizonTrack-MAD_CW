package com.example.horizontrack_mad_cw

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.horizontrack_mad_cw.databinding.ActivityTrackerBinding
import com.example.horizontrack_mad_cw.model.LocationModel
import com.example.horizontrack_mad_cw.model.SummaryModel
import com.google.type.LatLng
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.lang.Math.random
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class FitnessActivity : AppCompatActivity(), MapListener {

    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay
    private lateinit var polyline: Polyline
    private var SummaryModel = SummaryModel();

    private val locationHandler = Handler(Looper.getMainLooper())
    private val locationUpdateRunnable = object : Runnable {
        override fun run() {
            mMyLocationOverlay.myLocation?.let { location ->


                // TODO: COMMENT WHEN USIBNG REAL [MOCK]
                var mockLatitude = 37.4219999
                var mockLongitude = -122.0840575
                val random = Random()
                val randomLatitudeChange = (random.nextDouble() * 0.0002 - 0.0001)
                val randomLongitudeChange = (random.nextDouble() * 0.0002 - 0.0001)
                mockLatitude += randomLatitudeChange
                mockLongitude += randomLongitudeChange
                val location = GeoPoint(mockLatitude, mockLongitude)



                val updatedTime = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
                Toast.makeText(
                    this@FitnessActivity,
                    "Location updated at: $updatedTime",
                    Toast.LENGTH_SHORT
                ).show()
                controller.setZoom(30.0)
                controller.setCenter(location)
                SummaryModel.addLocation(LocationModel(LocalDateTime.now(),null,null,location.latitude,location.longitude),23/1000.0)
                addLocationToPolyline(location)
            }

            locationHandler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        mMap = binding.mapView
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        controller = mMap.controller

        val defaultLocation = GeoPoint(6.9271, 79.8612)
        controller.setCenter(defaultLocation)
        controller.setZoom(12.0)

        mMap.overlays.add(mMyLocationOverlay)
        mMap.addMapListener(this)

        polyline = Polyline()
        polyline.setColor(Color.RED)
        polyline.width = 5f
        mMap.overlays.add(polyline)

        checkLocationServices()
    }

    private fun checkLocationServices() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(
                this,
                "Please enable location services",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            enableLocationFeatures()
        }
    }

    private fun enableLocationFeatures() {
        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        locationHandler.post(locationUpdateRunnable)
    }

    private fun addLocationToPolyline(location: GeoPoint) {
        polyline.addPoint(location)
        mMap.invalidate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationFeatures()
            } else {
                Log.e("TAG", "Location permission denied")
            }
        }
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        Log.e("TAG", "onScroll: lat ${event?.source?.getMapCenter()?.latitude}")
        Log.e("TAG", "onScroll: lon ${event?.source?.getMapCenter()?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        Log.e("TAG", "onZoom zoom level: ${event?.zoomLevel} source: ${event?.source}")
        return false
    }

    override fun onDestroy() {
        locationHandler.removeCallbacks(locationUpdateRunnable)
        super.onDestroy()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
