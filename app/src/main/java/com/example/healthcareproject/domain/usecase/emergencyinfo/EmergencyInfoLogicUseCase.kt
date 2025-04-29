package com.example.healthcareproject.domain.usecase.emergencyinfo

import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class EmergencyInfoLogicUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
}