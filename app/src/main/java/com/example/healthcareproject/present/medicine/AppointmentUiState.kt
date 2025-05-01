package com.example.healthcareproject.present.medicine

import java.time.LocalDate
import java.time.LocalTime

data class AppointmentUiState(
    val diagnosis: String = "",
    val doctorName: String = "",
    val clinicName: String = "",
    val treatment: String = "",
    val visitDate: LocalDate? = null,
    val time: LocalTime? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val diagnosisError: String? = null,
    val doctorNameError: String? = null,
    val clinicNameError: String? = null,
    val treatmentError: String? = null,
    val dateError: String? = null,
    val timeError: String? = null
)