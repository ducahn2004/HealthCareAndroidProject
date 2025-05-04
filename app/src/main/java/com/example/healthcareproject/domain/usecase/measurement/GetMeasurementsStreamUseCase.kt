package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeasurementsStreamUseCase @Inject constructor(
    private val repository: MeasurementRepository
) {
    operator fun invoke(): Flow<List<Measurement>> {
        return repository.getMeasurementsRealtime()
    }
}