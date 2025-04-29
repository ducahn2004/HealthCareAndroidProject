package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.repository.MedicationRepository
import java.time.LocalDate
import javax.inject.Inject

class CreateMedicationUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository
) {
    suspend operator fun invoke(
        name: String,
        dosageUnit: DosageUnit,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: MealRelation,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String
    ): String {
        return medicationRepository.createMedication(
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
    }
}