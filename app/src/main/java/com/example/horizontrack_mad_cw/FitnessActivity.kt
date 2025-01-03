package com.example.horizontrack_mad_cw

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.horizontrack_mad_cw.databinding.ActivityTrackerBinding
import com.example.horizontrack_mad_cw.model.LocationModel
import com.example.horizontrack_mad_cw.model.SummaryModel
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class FitnessActivity : AppCompatActivity(), MapListener {

    lateinit var mMap: MapView
    lateinit var controller: IMapController
    lateinit var mMyLocationOverlay: MyLocationNewOverlay
    private lateinit var polyline: Polyline
    private var summaryModel = SummaryModel()
    private lateinit var marker: Marker

    private lateinit var totalDistanceText: TextView
    private lateinit var totalCalorieText: TextView
    private lateinit var avgSpeedText: TextView

    private lateinit var runningTimeText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var elapsedTime: Long = 0L
    private var startTime: Long = 0L
    private var isRunning: Boolean = true
    private lateinit var pauseButton: Button

    private val locationHandler = Handler(Looper.getMainLooper())
    private val locationUpdateRunnable = object : Runnable {
        var mockLatitude = 0.0
        var mockLongitude = 0.0
        var firstTime = true
        var initialized = false
        override fun run() {
            mMyLocationOverlay.myLocation?.let { location ->


                // TODO: COMMENT WHEN USING REAL [MOCK]
                if (firstTime) {
                    mockLongitude = location.longitude
                    mockLatitude = location.latitude
                    firstTime = false
                }
                val random = Random()
                val randomLatitudeChange = (random.nextDouble() * 0.0001 - 0.00005)
                val randomLongitudeChange = (random.nextDouble() * 0.0001 - 0.00005)
                mockLatitude += randomLatitudeChange
                mockLongitude += randomLongitudeChange
                val location = GeoPoint(mockLatitude, mockLongitude)


                if (!initialized) {
                    val updatedTime =
                        SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
                    Toast.makeText(
                        this@FitnessActivity,
                        "Location updated at: $updatedTime",
                        Toast.LENGTH_SHORT
                    ).show()
                    initialized=true
                }
                controller.setZoom(18.0)
                controller.setCenter(location)
                if (isRunning) {
                    summaryModel.addLocation(
                        LocationModel(
                            LocalDateTime.now(),
                            null,
                            null,
                            location.latitude,
                            location.longitude
                        ), 0.07
                    )
                    addLocationToPolyline(location)
                    marker?.position = location
                    updateSummaryDetails()
                }
            }

            locationHandler.postDelayed(this, 1000)
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
        polyline.color = Color.RED
        polyline.width = 5f
        mMap.overlays.add(polyline)

        marker = Marker(mMap)
        marker.position = defaultLocation
        marker.title = "Me"
        marker.icon = resources.getDrawable(R.drawable.ic_run_48__1_)
        mMap.overlays.add(marker)

        checkLocationServices()

        // Bind views
        totalDistanceText = findViewById(R.id.total_distance)
        totalCalorieText = findViewById(R.id.total_calorie)
        avgSpeedText = findViewById(R.id.avg_speed)

        runningTimeText = findViewById(R.id.running_time)
        pauseButton = findViewById(R.id.pause_button)

        startStopwatch()
        pauseButton.text = "STOP"

        pauseButton.setOnClickListener {
            if (pauseButton.text == "STOP") {
                stopStopwatch()
            } else {
                saveSummary()
            }
        }

    }

    private fun saveSummary() {
        Toast.makeText(
            this@FitnessActivity,
            "Saving To DB : Started",
            Toast.LENGTH_SHORT
        ).show()



    }

    private fun startStopwatch() {
        startTime = System.currentTimeMillis() - elapsedTime
        handler.post(updateTimerRunnable)
    }

    private fun stopStopwatch() {
        isRunning = false
        pauseButton.text = "SAVE SUMMARY"
    }

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                elapsedTime = currentTime - startTime
                val seconds = (elapsedTime / 1000) % 60
                val minutes = (elapsedTime / 1000) / 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                runningTimeText.text = timeString
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun updateSummaryDetails() {
        // Calculate average speed if speeds list is not empty
        val avgSpeed = if (summaryModel.getSpeeds().isNotEmpty()) {
            summaryModel.getSpeeds().average()
        } else {
            0.0
        }

        // Update UI elements
        // Update UI elements with rounded values
        totalDistanceText.text = "Total Distance: %.2f m".format(summaryModel.getTotalDistMeters())
        totalCalorieText.text = "Total Calorie: %.2f cal".format(summaryModel.getTotalCalorie())
        avgSpeedText.text = "Average Speed: %.2f m/s".format(avgSpeed)

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
//        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.icons8_camera)
//        mMyLocationOverlay.setPersonIcon(originalBitmap)
        mMyLocationOverlay.enableFollowLocation()
        locationHandler.post(locationUpdateRunnable)
    }

    private fun resizeDrawableToBitmap(drawable: Drawable, width: Int, height: Int): Bitmap {
        val bitmap = (drawable as BitmapDrawable).bitmap
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
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
        Log.e("TAG", "onScroll: lat ${event?.source?.mapCenter?.latitude}")
        Log.e("TAG", "onScroll: lon ${event?.source?.mapCenter?.longitude}")
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
