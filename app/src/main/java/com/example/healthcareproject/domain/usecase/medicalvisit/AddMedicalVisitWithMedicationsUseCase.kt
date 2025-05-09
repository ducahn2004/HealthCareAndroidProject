package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.usecase.medication.CreateMedicationUseCase
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class AddMedicalVisitWithMedicationsUseCase @Inject constructor(
    private val medicalVisitRepository: MedicalVisitRepository,
    private val medicationRepository: MedicationRepository
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
        medicalVisitRepository.withTransaction {
            // Lưu MedicalVisit với visitId
            medicalVisitRepository.createMedicalVisit(
                visitId = visitId,
                visitReason = visitReason,
                visitDate = visitDate,
                doctorName = doctorName,
                notes = diagnosis,
                status = status
            )

            // Lưu Medications với visitId
            medications.forEach { (name, data) ->
                medicationRepository.createMedication(
                    visitId = visitId,
                    name = name,
                    dosageUnit = data["dosageUnit"] as? DosageUnit ?: DosageUnit.None,
                    dosageAmount = (data["dosageAmount"] as? Number)?.toFloat() ?: 0f,
                    frequency = (data["frequency"] as? Number)?.toInt() ?: 1,
                    timeOfDay = data["timeOfDay"] as? List<String> ?: emptyList(),
                    mealRelation = data["mealRelation"] as? MealRelation ?: MealRelation.None,
                    startDate = data["startDate"] as? LocalDate ?: LocalDate.now(),
                    endDate = data["endDate"] as? LocalDate ?: LocalDate.now().plusMonths(1),
                    notes = data["notes"] as? String ?: ""
                )
            }
        }
    }
}