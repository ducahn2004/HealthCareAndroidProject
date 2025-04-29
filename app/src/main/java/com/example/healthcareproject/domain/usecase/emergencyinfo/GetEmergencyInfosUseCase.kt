package com.example.healthcareproject.domain.usecase.emergencyinfo

import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class GetEmergencyInfosUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(): List<EmergencyInfo> {
        return emergencyInfoRepository.getEmergencyInfos()
    }
}