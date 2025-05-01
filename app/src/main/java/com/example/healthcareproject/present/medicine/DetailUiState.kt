package com.example.healthcareproject.present.medicine

import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication

data class DetailUiState(
    val medicalVisit: MedicalVisit? = null,
    val medications: List<Medication> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val formattedDate: String? = null,
    val formattedTime: String? = null
)