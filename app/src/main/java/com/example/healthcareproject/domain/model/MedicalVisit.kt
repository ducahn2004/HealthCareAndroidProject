package com.example.healthcareproject.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
@Parcelize
data class MedicalVisit(
    val visitId: String,
    val userId: String,
    val visitDate: LocalDate,
    val clinicName: String,
    val doctorName: String,
    val diagnosis: String,
    val treatment: String,
    val createdAt: LocalDateTime
): Parcelable