package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.model.MeasurementType
import com.example.healthcareproject.domain.repository.MeasurementRepository
import java.time.LocalDateTime
import javax.inject.Inject

class CreateMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(
        type: MeasurementType,
        value: Float?,
        valueList: List<Float>?,
        measurementTime: LocalDateTime,
        status: Boolean
    ): String {
        return measurementRepository.createMeasurement(
            type = type,
            value = value,
            valueList = valueList,
            measurementTime = measurementTime,
            status = status
        )
    }
}