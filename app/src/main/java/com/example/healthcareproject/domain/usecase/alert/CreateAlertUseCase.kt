package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.repository.AlertRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CreateAlertUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    ): String {
        return alertRepository.createAlert(
            measurementId = measurementId,
            emergencyId = emergencyId,
            triggerReason = triggerReason,
            contacted = contacted,
            timestamp = timestamp
        )
    }
}