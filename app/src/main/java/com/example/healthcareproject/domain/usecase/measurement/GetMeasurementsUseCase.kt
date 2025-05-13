package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.repository.MeasurementRepository
import javax.inject.Inject

class GetMeasurementsUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(forceUpdate: Boolean = true): List<Measurement> {
        return measurementRepository.getMeasurements(forceUpdate)
    }
}