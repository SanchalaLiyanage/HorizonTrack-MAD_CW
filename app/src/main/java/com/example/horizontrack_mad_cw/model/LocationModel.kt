package com.example.horizontrack_mad_cw.model

import java.time.LocalDateTime

data class LocationModel(
    var time: String? = LocalDateTime.now().toString(),
    var note: String? = null,
    var imageb64: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "time" to time,
            "note" to note,
            "imageb64" to imageb64,
            "latitude" to latitude,
            "longitude" to longitude
        )
    }
}
