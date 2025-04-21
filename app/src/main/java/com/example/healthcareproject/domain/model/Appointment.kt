package com.example.healthcareproject.domain.model

import java.time.LocalDateTime

data class Appointment(
    val appointmentId: String,
    val userId: String,
    val visitId: String?,
    val doctorName: String,
    val location: String,
    val appointmentTime: LocalDateTime,
    val note: String
)