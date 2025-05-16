package com.example.healthcareproject.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
@Parcelize
data class Appointment(
    val appointmentId: String,
    val userId: String,
    val visitId: String?,
    val doctorName: String,
    val location: String,
    val appointmentTime: LocalDateTime,
    val note: String?
): Parcelable