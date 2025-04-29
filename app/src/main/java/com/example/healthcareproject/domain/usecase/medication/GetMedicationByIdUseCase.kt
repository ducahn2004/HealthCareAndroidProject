package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.repository.MedicationRepository
import javax.inject.Inject

class GetMedicationByIdUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(medicationId: String): Medication? {
        return medicationRepository.getMedication(medicationId)
    }
}