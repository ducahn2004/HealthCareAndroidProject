package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.AlertRepository
import java.time.LocalTime
import javax.inject.Inject

class CreateAlertUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(
        title: String,
        message: String,
        alertTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ): String {
        return alertRepository.createAlert(
            title = title,
            message = message,
            alertTime = alertTime,
            repeatPattern = repeatPattern,
            status = status
        )
    }
}