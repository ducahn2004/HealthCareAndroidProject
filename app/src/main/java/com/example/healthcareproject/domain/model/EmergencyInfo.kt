package com.example.healthcareproject.domain.model

data class EmergencyInfo(
    val emergencyId: String,
    val userId: String,
    val emergencyName: String,
    val emergencyPhone: String,
    val relationship: Relationship,
    val priority: Int
)