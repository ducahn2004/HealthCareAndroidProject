package com.example.healthcareproject.domain.model

import java.time.LocalDateTime

data class Measurement(
    val measurementId: String,
    val deviceId: String,
    val userId: String,
    val bpm: Float,
    val spO2: Float,
    val dateTime: LocalDateTime
)

