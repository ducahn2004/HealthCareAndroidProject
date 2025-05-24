package com.example.healthcareproject.data.source.network.model

data class FirebaseAlert(
    var alertId: String = "",
    var userId: String = "",
    var measurementId: String? = null,
    var emergencyId: String? = null,
    var triggerReason: String = "",
    var contacted: Boolean = false,
    val timestamp: String = ""
)