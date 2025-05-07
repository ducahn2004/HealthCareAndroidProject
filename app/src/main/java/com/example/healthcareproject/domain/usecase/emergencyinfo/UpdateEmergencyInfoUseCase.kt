package com.example.healthcareproject.domain.usecase.emergencyinfo

import com.example.healthcareproject.domain.model.Relationship
import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import javax.inject.Inject

class UpdateEmergencyInfoUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository
) {
    suspend operator fun invoke(
        emergencyInfoId: String,
        contactName: String,
        contactNumber: String,
        relationship: Relationship,
        priority : Int
    ) {
        emergencyInfoRepository.updateEmergencyInfo(
            emergencyInfoId = emergencyInfoId,
            contactName = contactName,
            contactNumber = contactNumber,
            relationship = relationship,
            priority = priority
        )
    }
}