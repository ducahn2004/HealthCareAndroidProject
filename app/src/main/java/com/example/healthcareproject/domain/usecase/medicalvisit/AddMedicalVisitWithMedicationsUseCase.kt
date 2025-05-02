package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.usecase.medication.CreateMedicationUseCase
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import java.time.LocalDate
import javax.inject.Inject

class AddMedicalVisitWithMedicationsUseCase @Inject constructor(
    private val createMedicalVisitUseCase: CreateMedicalVisitUseCase,
    private val createMedicationUseCase: CreateMedicationUseCase
) {
    suspend operator fun invoke(
        patientName: String,
        visitReason: String,
        visitDate: LocalDate,
        doctorName: String,
        notes: String?,
        status: Boolean,
        medications: List<Pair<String, Map<String, Any>>>
    ) {
        // Create MedicalVisit and get visitId
        val visitId = createMedicalVisitUseCase(
            patientName = patientName,
            visitReason = visitReason,
            visitDate = visitDate,
            doctorName = doctorName,
            notes = notes,
            status = status
        )

        // Add related Medications
        medications.forEach { (name, details) ->
            createMedicationUseCase(
                visitId = visitId,
                name = name,
                dosageUnit = details["dosageUnit"] as? DosageUnit ?: throw IllegalArgumentException("Invalid dosageUnit"),
                dosageAmount = details["dosageAmount"] as? Float ?: throw IllegalArgumentException("Invalid dosageAmount"),
                frequency = details["frequency"] as? Int ?: throw IllegalArgumentException("Invalid frequency"),
                timeOfDay = details["timeOfDay"] as? List<String> ?: emptyList(),
                mealRelation = details["mealRelation"] as? MealRelation ?: throw IllegalArgumentException("Invalid mealRelation"),
                startDate = details["startDate"] as? LocalDate ?: throw IllegalArgumentException("Invalid startDate"),
                endDate = details["endDate"] as? LocalDate ?: throw IllegalArgumentException("Invalid endDate"),
                notes = details["notes"] as? String ?: ""
            )
        }
    }
}