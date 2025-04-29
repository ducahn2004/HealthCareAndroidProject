package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.AlertRepository
import java.time.LocalTime
import javax.inject.Inject

class UpdateAlertUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(
        alertId: String,
        title: String,
        message: String,
        alertTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ) {
        alertRepository.updateAlert(
            alertId = alertId,
            title = title,
            message = message,
            alertTime = alertTime,
            repeatPattern = repeatPattern,
            status = status
        )
    }
}