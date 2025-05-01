package com.example.healthcareproject.present.medicine

import java.time.LocalDate
import java.time.LocalTime

data class AppointmentUiState(
    val diagnosis: String = "",
    val diagnosisError: String? = null,
    val doctorName: String = "",
    val doctorNameError: String? = null,
    val clinicName: String = "",
    val clinicNameError: String? = null,
    val treatment: String = "",
    val treatmentError: String? = null,
    val visitDate: LocalDate? = null,
    val dateError: String? = null,
    val time: LocalTime? = null,
    val timeError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)