package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.repository.MeasurementRepository
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(
        measurementId: String,
        bpm: Float,
        spO2: Float
    ) {
        measurementRepository.updateMeasurement(
            measurementId = measurementId,
            bpm = bpm,
            spO2 = spO2
        )
    }
}