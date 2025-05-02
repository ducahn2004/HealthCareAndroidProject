package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.model.MeasurementType
import com.example.healthcareproject.domain.repository.MeasurementRepository
import com.example.healthcareproject.domain.usecase.sos.SendSosUseCase
import com.example.healthcareproject.domain.usecase.user.GetUserUseCase
import kotlinx.coroutines.flow.mapNotNull
import java.util.Calendar
import javax.inject.Inject

class HRAnalysisUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val getUserUseCase: GetUserUseCase,
    private val sendSosUseCase: SendSosUseCase
) {
    private fun calculateAge(birthDate: String): Int {
        val birthYear = birthDate.split("-")[0].toInt()
        return Calendar.getInstance().get(Calendar.YEAR) - birthYear
    }

    private fun getNormalHeartRateRange(age: Int): Pair<Int, Int> {
        return when (age) {
            in 0..1 -> 120 to 160 // Infants
            in 2..12 -> 70 to 120 // Children
            else -> 60 to 100 // Adults and elderly
        }
    }

    suspend operator fun invoke() {
        val user = getUserUseCase(forceUpdate = true) ?: return
        val age = calculateAge(user.dateOfBirth.toString())
        val (minNormal, maxNormal) = getNormalHeartRateRange(age)

        measurementRepository.getMeasurementsStream()
            .mapNotNull { measurements ->
                measurements
                    .filter { it.type == MeasurementType.HR }
                    .maxByOrNull { it.timestamp } // Get the latest HR measurement
            }
            .collect { latestHR ->
                val heartRate = latestHR.value ?: return@collect
                if (heartRate < minNormal || heartRate > maxNormal) {
                    val triggerReason = "Abnormal heart rate detected: $heartRate bpm"
                    sendSosUseCase(
                        measurementId = latestHR.measurementId,
                        triggerReason = triggerReason
                    )
                }
            }
    }
}