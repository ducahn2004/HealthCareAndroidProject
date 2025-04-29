package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.repository.MedicationRepository
import javax.inject.Inject

class GetMedicationsUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(): List<Medication> {
        return medicationRepository.getMedications()
    }
}