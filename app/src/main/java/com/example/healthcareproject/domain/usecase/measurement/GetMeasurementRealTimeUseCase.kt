package com.example.healthcareproject.domain.usecase.measurement


import com.example.healthcareproject.domain.repository.MeasurementRepository
import javax.inject.Inject

class GetMeasurementRealTimeUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    operator fun invoke() = measurementRepository.getMeasurementsStream()
}