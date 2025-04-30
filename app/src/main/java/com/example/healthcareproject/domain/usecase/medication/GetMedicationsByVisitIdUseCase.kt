package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.repository.MedicationRepository
import javax.inject.Inject

class GetMedicationsByVisitIdUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(visitId: String): Result<List<Medication>> {
        return try {
            val medications = medicationRepository.getMedications()
                .filter { it.visitId == visitId }
            Result.Success(medications)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}