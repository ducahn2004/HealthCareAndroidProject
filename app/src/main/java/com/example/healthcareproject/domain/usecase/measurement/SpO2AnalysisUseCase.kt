package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.model.MeasurementType
import com.example.healthcareproject.domain.repository.MeasurementRepository
import com.example.healthcareproject.domain.usecase.sos.SendSosUseCase
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class SpO2AnalysisUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val sendSosUseCase: SendSosUseCase
) {
    suspend operator fun invoke() {
        // Observe real-time measurement stream
        measurementRepository.getMeasurementsStream()
            .mapNotNull { measurements ->
                measurements
                    .filter { it.type == MeasurementType.SpO2 }
                    .maxByOrNull { it.timestamp } // Get the latest SpO2 measurement
            }
            .collect { latestSpO2 ->
                val spO2Value = latestSpO2.value ?: return@collect

                // Analyze SpO2 levels and trigger SOS if necessary
                val triggerReason = when {
                    spO2Value < 90 -> "Critical Alert!!! Extremely low blood oxygen, seek emergency help."
                    spO2Value in 90.0..92.9 -> "Warning!!! Low blood oxygen, needs monitoring."
                    spO2Value in 93.0..95.9 -> "Warning!!! Moderate blood oxygen, consider supplemental oxygen."
                    spO2Value >= 96 -> null // No SOS needed
                    else -> return@collect
                }

                triggerReason?.let {
                    sendSosUseCase(
                        measurementId = latestSpO2.measurementId,
                        triggerReason = it
                    )
                }
            }
    }
}