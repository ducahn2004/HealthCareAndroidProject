package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateMedicalVisitUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository
) {
    suspend operator fun invoke(
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    ): String {
        return medicalVisitRepository.createMedicalVisit(
            patientName = patientName,
            visitReason = visitReason,
            visitDate = visitDate,
            doctorName = doctorName,
            notes = notes,
            status = status
        )
    }
}