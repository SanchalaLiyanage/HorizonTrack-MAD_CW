package com.example.horizontrack_mad_cw.model

import java.time.LocalDateTime

data class LocationModel(
    var time: LocalDateTime,
    var note: String?,
    var imageb64: String?,
    var latitude: Double,
    var longitude: Double
)