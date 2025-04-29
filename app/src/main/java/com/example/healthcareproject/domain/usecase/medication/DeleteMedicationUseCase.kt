package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.repository.MedicationRepository
import javax.inject.Inject

class DeleteMedicationUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(medicationId: String) {
        medicationRepository.deleteMedication(medicationId)
    }
}