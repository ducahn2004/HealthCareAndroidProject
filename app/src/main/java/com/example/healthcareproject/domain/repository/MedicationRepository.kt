package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface to the data layer for medications.
 */
interface MedicationRepository {

    suspend fun createMedication(
        name: String,
        dosageUnit: DosageUnit,
        dosageAmount: Float,
        frequency: Int,
        timeOfDay: List<String>,
        mealRelation: MealRelation,
        startDate: LocalDate,
        endDate: LocalDate,
        notes: String
    ): String

    suspend fun updateMedication(
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
    )

    fun getMedicationsStream(): Flow<List<Medication>>

    fun getMedicationStream(medicationId: String): Flow<Medication?>

    suspend fun getMedications(forceUpdate: Boolean = false): List<Medication>

    suspend fun getMedicationsByVisitId(visitId: String, forceUpdate: Boolean = false): List<Medication>

    suspend fun refresh()

    suspend fun getMedication(medicationId: String, forceUpdate: Boolean = false): Medication?

    suspend fun refreshMedication(medicationId: String)

    suspend fun activateMedication(medicationId: String)

    suspend fun deactivateMedication(medicationId: String)

    suspend fun clearInactiveMedications()

    suspend fun deleteAllMedications()

    suspend fun deleteMedication(medicationId: String)
}