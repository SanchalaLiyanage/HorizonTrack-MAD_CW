package com.example.horizontrack_mad_cw

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.horizontrack_mad_cw.model.LocationModel
import com.example.horizontrack_mad_cw.model.SummaryModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class SummaryFragment : Fragment() {

    private lateinit var summaryDropdown: Spinner
    private lateinit var totalCalories: TextView
    private lateinit var avgSpeed: TextView
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var speedGraph: LineChart
    private lateinit var mapView: MapView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.activity_fitness, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onCreate(savedInstanceState)

        // Initialize views
        summaryDropdown = view.findViewById(R.id.summary_dropdown)
        totalCalories = view.findViewById(R.id.total_calories)
        avgSpeed = view.findViewById(R.id.avg_speed)
        startTime = view.findViewById(R.id.start_time)
        endTime = view.findViewById(R.id.end_time)
        speedGraph = view.findViewById(R.id.speed_graph)
        mapView = view.findViewById(R.id.map_view)

        val trackBeginBtn: Button =
            view.findViewById(R.id.track_begin_btn)
        trackBeginBtn.setOnClickListener {
            val intent =
                Intent(view.context, FitnessActivity::class.java)
            view.context.startActivity(intent)
        }

        val refreshBtn: Button =
            view.findViewById(R.id.refresh)
        refreshBtn.setOnClickListener {
            fetchSummaries()
        }

        // Configure OSMdroid
        Configuration.getInstance()
            .load(requireContext(), requireActivity().getSharedPreferences("osm", MODE_PRIVATE))
        mapView.setMultiTouchControls(true)
        fetchSummaries()
    }

    private fun fetchSummaries() {
        Toast.makeText(
            activity,
            "Summary Loading . . .",
            Toast.LENGTH_SHORT
        ).show()
        val db = FirebaseFirestore.getInstance()
        val deviceId = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)

        db.collection("summaries")
            .whereEqualTo("deviceId", deviceId)
            .get()
            .addOnSuccessListener { result ->
                val summaries = result.toObjects(SummaryModel::class.java)
                setupDropdown(summaries)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    activity,
                    "Failed to load summaries: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setupDropdown(summaries: List<SummaryModel>) {
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                summaries.map { it.getId() })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        summaryDropdown.adapter = adapter

        summaryDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSummary = summaries[position]
                displaySummaryDetails(selectedSummary)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun displaySummaryDetails(summary: SummaryModel) {
        totalCalories.text = "${summary.getTotalCalorie()} cal"
        avgSpeed.text = "%.2f m/s".format(summary.getSpeeds().average())
        startTime.text = summary.getLocations().firstOrNull()?.time.toString() ?: "--:--:--"
        endTime.text = summary.getLocations().lastOrNull()?.time.toString() ?: "--:--:--"

        plotSpeedGraph(summary.getSpeeds())
        drawMap(summary.getLocations())
    }

    private fun plotSpeedGraph(speeds: List<Double>) {
        val entries = speeds.mapIndexed { index, speed -> Entry(index.toFloat(), speed.toFloat()) }
        val dataSet = LineDataSet(entries, "Speed").apply {
            color = resources.getColor(R.color.blue, null)
            valueTextColor = resources.getColor(R.color.black, null)
            lineWidth = 2f
            circleRadius = 4f
        }

        speedGraph.apply {
            data = LineData(dataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            description.isEnabled = false
            invalidate() // Refresh graph
        }
    }

    private fun drawMap(locations: List<LocationModel>) {
        mapView.overlays.clear()
        val geoPoints = locations.map { GeoPoint(it.latitude, it.longitude) }
        val line = Polyline().apply { setPoints(geoPoints) }
        line.color = Color.RED
        line.width = 5f
        mapView.overlays.add(line)

        locations.forEach { location ->
            if (location.note != null) {
                val geoPoint = GeoPoint(location.latitude, location.longitude)
                val marker = Marker(mapView).apply {
                    position = geoPoint
                    title = "Note"
                    snippet = location.note
                }
                mapView.overlays.add(marker)
            }
        }

        // Center map on the first location if available
        if (geoPoints.isNotEmpty()) {
            mapView.controller.setZoom(18.0)
            mapView.controller.setCenter(geoPoints.first())
        }
        mapView.invalidate()
    }
}
