package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.model.MeasurementType
import com.example.healthcareproject.domain.repository.MeasurementRepository
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateMeasurementUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository
) {
    suspend operator fun invoke(
        measurementId: String,
        type: MeasurementType,
        value: Float?,
        valueList: List<Float>?,
        measurementTime: LocalDateTime,
        status: Boolean
    ) {
        measurementRepository.updateMeasurement(
            measurementId = measurementId,
            type = type,
            value = value,
            valueList = valueList,
            measurementTime = measurementTime,
            status = status
        )
    }
}