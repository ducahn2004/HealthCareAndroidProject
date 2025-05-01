package com.example.healthcareproject.present.medicine

import com.example.healthcareproject.domain.model.MedicalVisit

data class MedicineUiState(
    val visitsBefore: List<MedicalVisit> = emptyList(),
    val visitsAfter: List<MedicalVisit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)