package com.example.healthcareproject.domain.usecase.sos

data class SosUseCases(
    val createSos: CreateSosUseCase,
    val getSosEvents: GetSosEventsUseCase,
    val updateSos: UpdateSosUseCase,
    val deleteSos: DeleteSosUseCase,
    val emergencyCall: SosEmergencyCallUseCase,
    val sendSos: SendSosUseCase
)
