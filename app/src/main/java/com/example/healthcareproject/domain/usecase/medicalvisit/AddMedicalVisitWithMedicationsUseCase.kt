package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.usecase.medication.CreateMedicationUseCase
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.model.Result
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import com.example.healthcareproject.domain.usecase.medication.MedicationUseCases
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject


class AddMedicalVisitWithMedicationsUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository,
    private val medicationUseCases: MedicationUseCases
) {
    suspend operator fun invoke(
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        diagnosis: String?,
        status: Boolean,
        medications: List<Pair<String, Map<String, Any>>>,
        visitId: String = UUID.randomUUID().toString()
    ) {
        Timber.d("Starting AddMedicalVisitWithMedicationsUseCase - VisitId: $visitId, VisitReason: $visitReason, DoctorName: $doctorName, Medications: ${medications.size}")

        try {
            require(visitReason.isNotBlank()) { "Visit reason cannot be empty" }
            require(doctorName.isNotBlank()) { "Doctor name cannot be empty" }

            medicalVisitRepository.withTransaction {
                // Tạo và đồng bộ MedicalVisit
                val savedVisitId = medicalVisitRepository.createMedicalVisit(
                    visitId = visitId,
                    visitReason = visitReason,
                    visitDate = visitDate,
                    doctorName = doctorName,
                    notes = diagnosis,
                    status = status
                )
                Timber.d("MedicalVisit created with ID: $savedVisitId")
                medicalVisitRepository.saveMedicalVisitsToNetwork()
                Timber.d("MedicalVisit synced to network")

                // Tạo Medications
                medications.forEachIndexed { index, (name, data) ->
                    Timber.d("Creating medication #$index: $name with visitId: $savedVisitId")
                    require(name.isNotBlank()) { "Medication name cannot be empty" }

                    val timeOfDay = when (val tod = data["timeOfDay"]) {
                        is String -> tod.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        is List<*> -> tod.filterIsInstance<String>().filter { it.isNotEmpty() }
                        else -> throw IllegalArgumentException("Time of day is required")
                    }

                    val dosageAmount = data["dosageAmount"] as? Number ?: throw IllegalArgumentException("dosageAmount must be a number for medication: $name")
                    val frequency = data["frequency"] as? Number ?: throw IllegalArgumentException("frequency must be a number for medication: $name")

                    val result = medicationUseCases.createMedication(
                        visitId = savedVisitId,
                        name = name,
                        dosageUnit = data["dosageUnit"] as? DosageUnit ?: DosageUnit.Mg,
                        dosageAmount = dosageAmount.toFloat(),
                        frequency = frequency.toInt(),
                        timeOfDay = timeOfDay,
                        mealRelation = data["mealRelation"] as? MealRelation ?: MealRelation.AfterMeal,
                        startDate = data["startDate"] as? LocalDate ?: LocalDate.now(),
                        endDate = data["endDate"] as? LocalDate ?: LocalDate.now().plusMonths(1),
                        notes = data["notes"] as? String ?: "",
                        syncToNetwork = false
                    )
                    when (result) {
                        is Result.Success -> Timber.d("Medication '$name' created successfully with ID: ${result.data}")
                        is Result.Error -> {
                            Timber.e(result.exception, "Failed to create medication '$name': ${result.exception.message}")
                            throw result.exception
                        }
                        else -> {
                            Timber.e("Unexpected result state for medication '$name': $result")
                            throw IllegalStateException("Unexpected result state for medication '$name': $result")
                        }
                    }
                }
                Timber.d("Syncing medications to network")

                val medicationsInRoom = medicationUseCases.getMedicationsByVisitId(savedVisitId)
                medicationsInRoom.forEach { med ->
                    if (med.visitId == null) {
                        Timber.e("Medication ${med.name} has null visitId after sync")
                        throw IllegalStateException("Medication ${med.name} has null visitId after sync")
                    }
                    Timber.d("Verified medication ${med.name} with visitId: ${med.visitId}")
                }
            }
            Timber.d("Operation completed successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error in AddMedicalVisitWithMedicationsUseCase: ${e.message}")
            throw e
        }
    }
}