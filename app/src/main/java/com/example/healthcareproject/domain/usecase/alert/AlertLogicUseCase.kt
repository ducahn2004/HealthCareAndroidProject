package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.repository.AlertRepository
import java.time.LocalDateTime
import javax.inject.Inject

class AlertLogicUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(triggerRinging: (Alert) -> Unit) {
        // Get all alerts with status = true
        val alerts = alertRepository.getAlerts(forceUpdate = true).filter { it.status }

        // Get the current date and time
        val currentDateTime = LocalDateTime.now()

        // Check each alert
        alerts.forEach { alert ->
            val alertDateTime = currentDateTime.with(alert.alertTime)
            if (currentDateTime.isAfter(alertDateTime) || currentDateTime.isEqual(alertDateTime)) {
                // Trigger the ringing action
                triggerRinging(alert)
            }
        }
    }
}