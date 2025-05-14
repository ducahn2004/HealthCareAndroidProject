package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.repository.AlertRepository
import javax.inject.Inject

class DeleteAlertUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    suspend operator fun invoke(alertId: String) {
        alertRepository.deleteAlert(alertId)
    }
}