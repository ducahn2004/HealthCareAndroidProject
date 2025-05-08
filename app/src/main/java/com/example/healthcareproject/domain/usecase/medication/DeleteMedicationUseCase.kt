package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.model.Result
import javax.inject.Inject

class DeleteMedicationUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(medicationId: String): Result<Unit> {
        return try {
            require(medicationId.isNotBlank()) { "Medication ID cannot be empty" }
            val existingMedication = medicationRepository.getMedication(medicationId)
            if (existingMedication == null) {
                Result.Error(Exception("Medication not found"))
            } else {
                medicationRepository.deleteMedication(medicationId)
                Result.Success(Unit)
            }
        } catch (e: IllegalArgumentException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to delete medication: ${e.message}", e))
        }
    }
}