package com.example.healthcareproject.domain.usecase.emergencyinfo

import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class GetEmergencyInfoByIdUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(emergencyInfoId: String): EmergencyInfo? {
        return emergencyInfoRepository.getEmergencyInfo(emergencyInfoId)
    }
}