package com.example.healthcareproject.data.source.network.model

data class FirebaseMeasurement(
    var measurementId: String = "",
    var userId: String = "",
    var bpm: Float = 0.0f,
    var spO2: Float = 0.0f,
)

