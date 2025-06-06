package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.repository.MedicationRepository
import javax.inject.Inject

class GetMedicationsUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(): Result<List<Medication>> {
        return try {
            Result.Success(medicationRepository.getMedications())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}