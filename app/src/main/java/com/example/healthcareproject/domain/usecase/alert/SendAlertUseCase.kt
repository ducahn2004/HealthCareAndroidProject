package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import java.time.LocalDateTime
import javax.inject.Inject

class SendAlertUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository,
    private val createAlertUseCase: CreateAlertUseCase,
    private val alertCallUseCase: AlertCallUseCase
) {
    suspend operator fun invoke(measurementId: String, triggerReason: String) {
        // Retrieve the list of emergency contacts sorted by priority
        val emergencyInfos = emergencyInfoRepository.getEmergencyInfos()
            .sortedBy { it.priority }

        // Call the first contact in the sorted list
        emergencyInfos.firstOrNull()?.let { emergencyInfo ->

            // Create a new alert event
            createAlertUseCase(
                measurementId = measurementId,
                emergencyId = emergencyInfo.emergencyId,
                triggerReason = triggerReason,
                contacted = true,
                timestamp = LocalDateTime.now()
            )

            alertCallUseCase.call(emergencyInfo.emergencyPhone)
        }
    }
}