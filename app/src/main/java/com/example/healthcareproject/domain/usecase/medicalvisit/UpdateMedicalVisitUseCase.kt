package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import java.time.LocalDate
import javax.inject.Inject

class UpdateMedicalVisitUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository
) {
    suspend operator fun invoke(
        medicalVisitId: String,
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean
    ) {
        medicalVisitRepository.updateMedicalVisit(
            medicalVisitId = medicalVisitId,
            patientName = patientName,
            visitReason = visitReason,
            visitDate = visitDate,
            doctorName = doctorName,
            notes = notes,
            status = status
        )
    }
}