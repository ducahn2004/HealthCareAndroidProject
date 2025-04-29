package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.repository.MeasurementRepository
import javax.inject.Inject

class DeleteMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(measurementId: String) {
        measurementRepository.deleteMeasurement(measurementId)
    }
}