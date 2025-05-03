package com.example.healthcareproject.domain.usecase.measurement

data class MeasurementUseCases(
    val getMeasurementsUseCase: GetMeasurementsUseCase,
    val createMeasurementUseCase: CreateMeasurementUseCase,
    val deleteMeasurementUseCase: DeleteMeasurementUseCase,
    val updateMeasurementUseCase: UpdateMeasurementUseCase,
    val hRAnalysisUseCase: HRAnalysisUseCase,
    val spO2AnalysisUseCase: SpO2AnalysisUseCase
)
