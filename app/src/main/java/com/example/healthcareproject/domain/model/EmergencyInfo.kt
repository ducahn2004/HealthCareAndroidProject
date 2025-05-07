package com.example.healthcareproject.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmergencyInfo(
    val emergencyId: String,
    val userId: String,
    val emergencyName: String,
    val emergencyPhone: String,
    val relationship: Relationship,
    val priority: Int
): Parcelable