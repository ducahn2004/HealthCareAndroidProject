package com.example.healthcareproject.domain.usecase.measurement

import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.UserRepository
import java.util.Calendar
import javax.inject.Inject

class HRAnalysisUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val medicationRepository: MedicalVisitRepository
) {
    private fun calculateAge(birthDate: String?): Int {
        if (birthDate.isNullOrEmpty()) return 0
        val birthYear = birthDate.split("-").getOrNull(0)?.toIntOrNull() ?: return 0
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
        val medicalVisits = medicationRepository.getMedicalVisits(forceUpdate = true)
        var adjustedMin = minNormal
        var adjustedMax = maxNormal

        medicalVisits.forEach { visit ->
            val diagnosis = visit.diagnosis.lowercase()
            if (visit.treatment == "Inactive") {
                when (diagnosis) {
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

    suspend fun isAbnormal(heartRate: Float): Boolean {
        val user = userRepository.getUser(forceUpdate = true)
        val age = calculateAge(user?.dateOfBirth.toString())
        val (minNormal, maxNormal) = getNormalHeartRateRange(age)
        val (adjustedMin, adjustedMax) = adjustHeartRateRangeBasedOnMedicalHistory(
            minNormal,
            maxNormal
        )

        return heartRate !in adjustedMin.toFloat()..adjustedMax.toFloat()
    }
}