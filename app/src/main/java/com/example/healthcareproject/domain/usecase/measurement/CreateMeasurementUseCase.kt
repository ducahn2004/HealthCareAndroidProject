package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.repository.MeasurementRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CreateMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(
        bpm: Float,
        spO2: Float,
        status: Boolean
    ): String {
        return measurementRepository.createMeasurement(
            bpm = bpm,
            spO2 = spO2,
            status = status
        )
    }
}