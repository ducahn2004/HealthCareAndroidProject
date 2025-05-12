package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.model.Result
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class CreateMedicationUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(
        visitId: String?,
        name: String,
        dosageUnit: DosageUnit,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: MealRelation,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String,
        syncToNetwork: Boolean = true
    ): Result<String> {
        return try {
            require(name.isNotBlank()) { "Medication name cannot be empty" }
            require(dosageAmount > 0) { "Dosage amount must be positive" }
            require(frequency > 0) { "Frequency must be positive" }
            require(timeOfDay.isNotEmpty()) { "At least one time of day is required" }
            require(startDate <= endDate) { "Start date must be before or equal to end date" }
            timeOfDay.forEach { time ->
                require(isValidTimeFormat(time)) { "Invalid time format: $time" }
            }

            val medicationId = medicationRepository.createMedication(
                visitId = visitId,
                name = name,
                dosageUnit = dosageUnit,
                dosageAmount = dosageAmount,
                frequency = frequency,
                timeOfDay = timeOfDay,
                mealRelation = mealRelation,
                startDate = startDate,
                endDate = endDate,
                notes = notes,
                syncToNetwork = syncToNetwork
            )
            Result.Success(medicationId)
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Validation failed: ${e.message}")
            Result.Error(e)
        } catch (e: Exception) {
            Timber.e(e, "Failed to create medication: ${e.message}")
            Result.Error(Exception("Failed to create medication: ${e.message}", e))
        }
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return try {
            LocalTime.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }
}