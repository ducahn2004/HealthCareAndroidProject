package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import javax.inject.Inject

class GetMedicalVisitUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository
) {
    suspend operator fun invoke(medicalVisitId: String, forceUpdate: Boolean = false): MedicalVisit? {
        return medicalVisitRepository.getMedicalVisit(medicalVisitId, forceUpdate)
    }
}