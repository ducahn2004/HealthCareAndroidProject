package com.example.healthcareproject.present.setting

import java.io.Serializable

data class EmergencyContact(
    val id: Long = System.currentTimeMillis(), // ID duy nhất
    val name: String,
    val phoneNumber: String,
    val relationship: String,
    val priority: Int // 1 (thấp) đến 5 (cao)
) : Serializable