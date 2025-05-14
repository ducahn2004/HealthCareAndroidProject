package com.example.healthcareproject.domain.usecase.alert

data class AlertUseCases(
    val createAlert: CreateAlertUseCase,
    val deleteAlert: DeleteAlertUseCase,
    val emergencyCall: AlertCallUseCase,
    val sendAlert: SendAlertUseCase
)