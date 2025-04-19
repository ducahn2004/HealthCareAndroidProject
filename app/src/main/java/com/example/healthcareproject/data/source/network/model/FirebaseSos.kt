package com.example.healthcareproject.data.source.network.model

data class FirebaseSos(
    var sosId: String = "",
    var userId: String = "",
    var measurementId: String? = null,
    var emergencyId: String? = null,
    var triggerReason: String = "",
    var contacted: Boolean = false,
    val timestamp: String
)
