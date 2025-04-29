package com.example.healthcareproject.domain.usecase.emergencyinfo

import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class DeleteEmergencyInfoUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(emergencyInfoId: String) {
        emergencyInfoRepository.deleteEmergencyInfo(emergencyInfoId)
    }
}