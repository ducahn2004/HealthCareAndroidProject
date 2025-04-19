package com.example.healthcareproject.data.source.network.model

data class FirebaseEmergencyInfo(
    var emergencyId: String = "",
    var userId: String = "",
    var emergencyName: String = "",
    var emergencyPhone: String = "",
    var relationship: String = "",
    var priority: Int
)
