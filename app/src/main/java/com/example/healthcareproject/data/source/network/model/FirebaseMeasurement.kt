package com.example.healthcareproject.data.source.network.model

data class FirebaseMeasurement(
    var id: String = "",
    var userId: String = "",
    var type: String = "",
    var value: Double = 0.0,
    var timestamp: String = ""
)

