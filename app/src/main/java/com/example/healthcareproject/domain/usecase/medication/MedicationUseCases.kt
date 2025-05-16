package com.example.healthcareproject.domain.usecase.medication

data class MedicationUseCases(
    val createMedication: CreateMedicationUseCase,
    val getMedications: GetMedicationsUseCase,
    val updateMedication: UpdateMedicationUseCase,
    val deleteMedication: DeleteMedicationUseCase,
    val getMedicationById: GetMedicationByIdUseCase,
    val getMedicationsByVisitId: GetMedicationsByVisitIdUseCase,
)