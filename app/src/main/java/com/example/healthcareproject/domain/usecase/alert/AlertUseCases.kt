package com.example.healthcareproject.domain.usecase.alert

data class AlertUseCases(
    val createAlert: CreateAlertUseCase,
    val getAlerts: GetAlertsUseCase,
    val updateAlert: UpdateAlertUseCase,
    val deleteAlert: DeleteAlertUseCase,
    val getAlertById: GetAlertByIdUseCase,
    val alertLogic: AlertLogicUseCase
)