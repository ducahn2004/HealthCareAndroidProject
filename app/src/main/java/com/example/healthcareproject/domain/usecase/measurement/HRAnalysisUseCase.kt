package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.repository.MeasurementRepository
import com.example.healthcareproject.domain.usecase.medicalvisit.GetMedicalVisitsUseCase
import com.example.healthcareproject.domain.usecase.alert.SendAlertUseCase
import com.example.healthcareproject.domain.usecase.user.GetUserUseCase
import kotlinx.coroutines.flow.mapNotNull
import java.util.Calendar
import javax.inject.Inject

class HRAnalysisUseCase @Inject constructor(
    private val measurementRepository: MeasurementRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getMedicalVisitUseCase: GetMedicalVisitsUseCase,
    private val sendAlertUseCase: SendAlertUseCase
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

    private suspend fun adjustHeartRateRangeBasedOnMedicalHistory(
        minNormal: Int,
        maxNormal: Int
    ): Pair<Int, Int> {
        val medicalVisits = getMedicalVisitUseCase(forceUpdate = true)
        var adjustedMin = minNormal
        var adjustedMax = maxNormal

        medicalVisits.forEach { visit ->
            if (visit.treatment == "Inactive") {
                when (visit.diagnosis.lowercase()) {
                    "hypertension" -> adjustedMax += 10
                    "hypotension" -> adjustedMin -= 10
                    "arrhythmia" -> {
                        adjustedMin -= 5
                        adjustedMax += 5
                    }
                }
            }
        }
        return adjustedMin to adjustedMax
    }

    suspend operator fun invoke() {
        val user = getUserUseCase(forceUpdate = true) ?: return
        val age = calculateAge(user.dateOfBirth.toString())
        val (minNormal, maxNormal) = getNormalHeartRateRange(age)
        val (adjustedMin, adjustedMax) = adjustHeartRateRangeBasedOnMedicalHistory(minNormal, maxNormal)

        measurementRepository.getMeasurementsStream()
            .mapNotNull { measurements ->
                measurements
                    .maxByOrNull { it.measurementId }
            }
            .collect { latestHR ->
                val heartRate = latestHR.bpm
                if (heartRate < adjustedMin || heartRate > adjustedMax) {
                    val triggerReason = "Abnormal heart rate detected: $heartRate bpm"
                    sendAlertUseCase(
                        measurementId = latestHR.measurementId,
                        triggerReason = triggerReason
                    )
                }
            }
    }
}