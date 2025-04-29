package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import javax.inject.Inject

class DeleteMedicalVisitUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository
) {
    suspend operator fun invoke(medicalVisitId: String) {
        medicalVisitRepository.deleteMedicalVisit(medicalVisitId)
    }
}