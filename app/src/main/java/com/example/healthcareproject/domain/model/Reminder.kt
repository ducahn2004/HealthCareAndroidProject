package com.example.healthcareproject.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Parcelize
data class Reminder(
    val reminderId: String,
    val userId: String,
    val title: String,
    val message: String,
    val reminderTime: LocalTime,
    val repeatPattern: RepeatPattern,
    val status: Boolean,
    val createdAt: LocalDateTime,
    val startDate: LocalDate,
    val endDate: LocalDate
): Parcelable