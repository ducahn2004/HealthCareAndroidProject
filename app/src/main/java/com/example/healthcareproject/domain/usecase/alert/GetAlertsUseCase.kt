package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.repository.AlertRepository
import javax.inject.Inject

class GetAlertsUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(): List<Alert> {
        return alertRepository.getAlerts()
    }
}