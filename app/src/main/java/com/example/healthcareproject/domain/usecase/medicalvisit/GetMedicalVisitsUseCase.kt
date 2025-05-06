package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import javax.inject.Inject

class GetMedicalVisitsUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository
) {
    suspend operator fun invoke(forceUpdate: Boolean = true): List<MedicalVisit> {
        return medicalVisitRepository.getMedicalVisits(forceUpdate)
    }
}