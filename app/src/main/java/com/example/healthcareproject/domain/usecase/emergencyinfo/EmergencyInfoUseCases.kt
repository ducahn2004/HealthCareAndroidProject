package com.example.healthcareproject.domain.usecase.emergencyinfo

data class EmergencyInfoUseCases(
    val createEmergencyInfo: CreateEmergencyInfoUseCase,
    val getEmergencyInfos: GetEmergencyInfosUseCase,
    val updateEmergencyInfo: UpdateEmergencyInfoUseCase,
    val deleteEmergencyInfo: DeleteEmergencyInfoUseCase,
    val getEmergencyInfoById: GetEmergencyInfoByIdUseCase,
    val emergencyInfoLogic: EmergencyInfoLogicUseCase
)
