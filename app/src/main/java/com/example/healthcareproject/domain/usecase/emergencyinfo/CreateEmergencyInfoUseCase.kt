package com.example.healthcareproject.domain.usecase.emergencyinfo

import com.example.healthcareproject.domain.model.Relationship
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class CreateEmergencyInfoUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(
        contactName: String,
        contactNumber: String,
        relationship: Relationship,
        priority : Int
    ): String {
        return emergencyInfoRepository.createEmergencyInfo(
            contactName = contactName,
            contactNumber = contactNumber,
            relationship = relationship,
            priority = priority
        )
    }
}