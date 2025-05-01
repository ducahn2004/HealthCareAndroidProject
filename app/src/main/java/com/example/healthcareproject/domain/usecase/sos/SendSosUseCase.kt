package com.example.healthcareproject.domain.usecase.sos

import com.example.healthcareproject.domain.repository.EmergencyInfoRepository
import java.time.LocalDateTime
import javax.inject.Inject

class SendSosUseCase @Inject constructor(
    private val emergencyInfoRepository: EmergencyInfoRepository,
    private val createSosUseCase: CreateSosUseCase,
    private val sosEmergencyCallUseCase: SosEmergencyCallUseCase
) {
    suspend operator fun invoke(measurementId: String, triggerReason: String) {
        // Retrieve the list of emergency contacts sorted by priority
        val emergencyInfos = emergencyInfoRepository.getEmergencyInfos()
            .sortedBy { it.priority }

        // Call the first contact in the sorted list
        emergencyInfos.firstOrNull()?.let { emergencyInfo ->

            // Create a new SOS event
            createSosUseCase(
                measurementId = measurementId,
                emergencyId = emergencyInfo.emergencyId,
                triggerReason = triggerReason,
                contacted = true,
                timestamp = LocalDateTime.now()
            )

            sosEmergencyCallUseCase.call(emergencyInfo.emergencyPhone)
        }
    }
}