package com.example.healthcareproject.domain.usecase.medicalvisit

data class MedicalVisitUseCases(
    val getMedicalVisitsUseCase: GetMedicalVisitsUseCase,
    val getMedicalVisitUseCase: GetMedicalVisitUseCase,
    val createMedicalVisitUseCase: CreateMedicalVisitUseCase,
    val updateMedicalVisitUseCase: UpdateMedicalVisitUseCase,
    val deleteMedicalVisitUseCase: DeleteMedicalVisitUseCase
)