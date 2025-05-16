package com.example.healthcareproject.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

@Parcelize
data class Reminder(
    val reminderId: String,
    val userId: String,
    val title: String,
    val message: String,
    val alertTime: LocalTime,
    val repeatPattern: RepeatPattern,
    val status: Boolean,
    val createdAt: LocalDateTime
): Parcelable