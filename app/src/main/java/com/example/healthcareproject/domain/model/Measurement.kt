package com.example.healthcareproject.domain.model

import java.time.LocalDateTime

data class Measurement(
    val measurementId: String,
    val userId: String,
    val type: MeasurementType,
    val value: Float?,
    val valueList: List<Float>?,
    val timestamp: LocalDateTime
)