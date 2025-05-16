package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.model.Result
import java.util.UUID
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateMedicalVisitUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository
) {
    suspend operator fun invoke(
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        diagnosis: String?,
        status: Boolean = true
    ): Result<String> {
        return try {
            if (visitReason.isBlank()) throw IllegalArgumentException("Visit reason cannot be empty")
            if (doctorName.isBlank()) throw IllegalArgumentException("Doctor name cannot be empty")
            if (visitDate.isAfter(LocalDate.now())) throw IllegalArgumentException("Visit date cannot be in the feature")

            val notes = buildString {
                if (!diagnosis.isNullOrBlank()) append("Diagnosis: $diagnosis\n")
            }.takeIf { it.isNotBlank() }
            val visitId = UUID.randomUUID().toString()

            val resultVisitId = medicalVisitRepository.createMedicalVisit(
                visitId = visitId,
                visitReason = visitReason,
                visitDate = visitDate,
                doctorName = doctorName,
                notes = notes,
                status = status
            )

            Result.Success(visitId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}