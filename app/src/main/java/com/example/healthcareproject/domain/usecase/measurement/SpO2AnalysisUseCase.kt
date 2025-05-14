package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.repository.MeasurementRepository
import com.example.healthcareproject.domain.usecase.alert.SendAlertUseCase
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class SpO2AnalysisUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val sendAlertUseCase: SendAlertUseCase
) {
    suspend operator fun invoke() {
//         Observe real-time measurement stream
        measurementRepository.getMeasurementsStream()
            .mapNotNull { measurements ->
                measurements
                    .maxByOrNull { it.measurementId } // Get the latest SpO2 measurement
            }
            .collect { latestSpO2 ->
                val spO2Value = latestSpO2.spO2

//                 Analyze SpO2 levels and trigger SOS if necessary
                val triggerReason = when {
                    spO2Value < 90 -> "Critical Alert!!! Extremely low blood oxygen, seek emergency help."
                    spO2Value in 90.0..92.9 -> "Warning!!! Low blood oxygen, needs monitoring."
                    spO2Value in 93.0..95.9 -> "Warning!!! Moderate blood oxygen, consider supplemental oxygen."
                    spO2Value >= 96 -> null // No SOS needed
                    else -> return@collect
                }

                triggerReason?.let {
                    sendAlertUseCase(
                        measurementId = latestSpO2.measurementId,
                        triggerReason = it
                    )
                }
            }
    }
}