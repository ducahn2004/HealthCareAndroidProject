package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.repository.MedicationRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateMedicationUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(
        name: String,
        dosageUnit: String,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: String?,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String
    ): Result<String> {
        return try {
            // Validate inputs
            if (name.isBlank()) throw IllegalArgumentException("Medication name cannot be empty")
            if (dosageUnit.isBlank()) throw IllegalArgumentException("Dosage unit cannot be empty")
            if (dosageAmount <= 0) throw IllegalArgumentException("Dosage amount must be positive")
            if (frequency <= 0) throw IllegalArgumentException("Frequency must be positive")
            if (timeOfDay.isEmpty()) throw IllegalArgumentException("Time of day cannot be empty")
            if (startDate.isAfter(endDate)) throw IllegalArgumentException("Start date cannot be after end date")

            // Convert String to DosageUnit (assuming DosageUnit is an enum)
            val parsedDosageUnit = try {
                DosageUnit.valueOf(dosageUnit.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid dosage unit: $dosageUnit")
            }

            // Convert String? to MealRelation (assuming MealRelation is an enum)
            val parsedMealRelation = mealRelation?.let {
                try {
                    MealRelation.valueOf(it.uppercase())
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException("Invalid meal relation: $it")
                }
            } ?: MealRelation.None// Default to NONE if mealRelation is null

            // Call repository to create medication
            val medicationId = medicationRepository.createMedication(
                name = name,
                dosageUnit = parsedDosageUnit,
                dosageAmount = dosageAmount,
                frequency = frequency,
                timeOfDay = timeOfDay,
                mealRelation = parsedMealRelation,
                startDate = startDate,
                endDate = endDate,
                notes = notes
            )

            Result.Success(medicationId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}