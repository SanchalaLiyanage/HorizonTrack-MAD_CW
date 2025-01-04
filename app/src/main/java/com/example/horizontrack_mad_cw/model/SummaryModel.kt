package com.example.horizontrack_mad_cw.model

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class SummaryModel(
    private var id: String = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault()).format(Date()),
    private var locations: MutableList<LocationModel> = mutableListOf(),
    private var speeds: MutableList<Double> = mutableListOf(),
    private var totalCalorie: Double = 0.0,
    private var totalDistMeters: Double = 0.0
) {

    fun getId(): String = id
    fun getLocations(): List<LocationModel> = locations
    fun getSpeeds(): List<Double> = speeds
    fun getTotalCalorie(): Double = totalCalorie
    fun getTotalDistMeters(): Double = totalDistMeters

    fun addLocation(location: LocationModel, caloriePerMeter: Double) {
        if (locations.isNotEmpty()) {
            val prevLocation = locations.last()

            val distance = calculateDistance(
                prevLocation.latitude, prevLocation.longitude,
                location.latitude, location.longitude
            )
            totalDistMeters += distance

            val timeDifferenceMillis = calculateTimeDifferenceInMilliSecs(
                LocalDateTime.parse(prevLocation.time), LocalDateTime.parse(location.time)
            )
            // Calculate speed in meters per second (m/s)
            val speed = if (timeDifferenceMillis > 0) (distance / timeDifferenceMillis) * 1000 else 0.0
            speeds.add(speed)

            totalCalorie += distance * caloriePerMeter
        } else {
            speeds.add(0.0)
        }

        locations.add(location)
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Double {
        val radius = 6371e3 // Radius of Earth in meters
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(lon2 - lon1)

        val a = sin(deltaLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radius * c
    }

    private fun calculateTimeDifferenceInMilliSecs(startTime: LocalDateTime, endTime: LocalDateTime?): Double {
        if (startTime == null || endTime == null) {
            return 0.0 // Default to 0 if either time is null
        }
        val duration = java.time.Duration.between(startTime, endTime)
        return duration.toMillis().toDouble()
    }


}