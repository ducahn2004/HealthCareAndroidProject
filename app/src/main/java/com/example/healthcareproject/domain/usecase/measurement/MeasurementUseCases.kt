package com.example.healthcareproject.domain.usecase.measurement

data class MeasurementUseCases(
    val getMeasurementsUseCase: GetMeasurementsUseCase,
    val getMeasurementRealTimeUseCase: GetMeasurementRealTimeUseCase,
    val deleteMeasurementUseCase: DeleteMeasurementUseCase,
    val hRAnalysisUseCase: HRAnalysisUseCase,
    val spO2AnalysisUseCase: SpO2AnalysisUseCase
)
