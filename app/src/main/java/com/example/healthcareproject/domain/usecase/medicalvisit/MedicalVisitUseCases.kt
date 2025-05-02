package com.example.healthcareproject.domain.usecase.medicalvisit

data class MedicalVisitUseCases(
    val createMedicalVisitUseCase: CreateMedicalVisitUseCase,
    val getMedicalVisitsUseCase: GetMedicalVisitsUseCase,
    val getMedicalVisitUseCase: GetMedicalVisitUseCase,
    val updateMedicalVisitUseCase: UpdateMedicalVisitUseCase,
    val deleteMedicalVisitUseCase: DeleteMedicalVisitUseCase,
    val addMedicalVisitWithMedicationsUseCase: AddMedicalVisitWithMedicationsUseCase
)