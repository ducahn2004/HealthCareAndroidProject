package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.model.Result
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class UpdateMedicationUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(
        medicationId: String,
        name: String,
        dosageUnit: DosageUnit,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: MealRelation,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String
    ): Result<Unit> {
        return try {
            // Kiểm tra dữ liệu đầu vào
            require(medicationId.isNotBlank()) { "Medication ID cannot be empty" }
            require(name.isNotBlank()) { "Medication name cannot be empty" }
            require(dosageAmount > 0) { "Dosage amount must be positive" }
            require(frequency > 0) { "Frequency must be positive" }
            require(timeOfDay.isNotEmpty()) { "At least one time of day is required" }
            require(startDate <= endDate) { "Start date must be before or equal to end date" }
            timeOfDay.forEach { time ->
                require(isValidTimeFormat(time)) { "Invalid time format: $time" }
            }

            // Kiểm tra sự tồn tại của thuốc
            val existingMedication = medicationRepository.getMedication(medicationId)
            if (existingMedication == null) {
                Result.Error(Exception("Medication not found"))
            } else {
                medicationRepository.updateMedication(
                    medicationId = medicationId,
                    name = name,
                    dosageUnit = dosageUnit,
                    dosageAmount = dosageAmount,
                    frequency = frequency,
                    timeOfDay = timeOfDay,
                    mealRelation = mealRelation,
                    startDate = startDate,
                    endDate = endDate,
                    notes = notes
                )
                Result.Success(Unit)
            }
        } catch (e: IllegalArgumentException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(Exception("Failed to update medication: ${e.message}", e))
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