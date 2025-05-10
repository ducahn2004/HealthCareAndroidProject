package com.example.healthcareproject.domain.usecase.medicalvisit

import com.example.healthcareproject.domain.usecase.medication.CreateMedicationUseCase
import com.example.healthcareproject.domain.model.DosageUnit
import com.example.healthcareproject.domain.model.MealRelation
import com.example.healthcareproject.domain.model.MedicalVisit
import com.example.healthcareproject.domain.model.Medication
import com.example.healthcareproject.domain.repository.MedicalVisitRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import timber.log.Timber
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
        // Thêm debug log
        Timber.d("Starting AddMedicalVisitWithMedicationsUseCase operation")
        Timber.d("VisitId: $visitId, VisitReason: $visitReason, DoctorName: $doctorName")
        Timber.d("Number of medications: ${medications.size}")

        try {
            // First: Ensure MedicalVisit is saved
            val savedVisitId = medicalVisitRepository.createMedicalVisit(
                visitId = visitId,
                visitReason = visitReason,  // Truyền đúng visitReason (sẽ được lưu là clinicName)
                visitDate = visitDate,
                doctorName = doctorName,
                notes = diagnosis,
                status = status
            )

            Timber.d("MedicalVisit created with ID: $savedVisitId, now creating medications")

            // Second: Save each medication with reference to the visit
            medications.forEach { (name, data) ->
                // Chuyển đổi timeOfDay thành danh sách nếu cần
                val timeOfDay = when (val tod = data["timeOfDay"]) {
                    is String -> tod.split(",").map { it.trim() }
                    is List<*> -> tod.filterIsInstance<String>()
                    else -> emptyList<String>()
                }

                Timber.d("Creating medication: $name with visitId: $savedVisitId")

                try {
                    medicationRepository.createMedication(
                        visitId = savedVisitId,
                        name = name,
                        dosageUnit = data["dosageUnit"] as? DosageUnit ?: DosageUnit.None,
                        dosageAmount = (data["dosageAmount"] as? Number)?.toFloat() ?: 0f,
                        frequency = (data["frequency"] as? Number)?.toInt() ?: 1,
                        timeOfDay = timeOfDay,
                        mealRelation = data["mealRelation"] as? MealRelation ?: MealRelation.None,
                        startDate = data["startDate"] as? LocalDate ?: LocalDate.now(),
                        endDate = data["endDate"] as? LocalDate ?: LocalDate.now().plusMonths(1),
                        notes = data["notes"] as? String ?: ""
                    )
                    Timber.d("Medication '$name' created successfully")
                } catch (e: Exception) {
                    Timber.e(e, "Error creating medication: $name - ${e.message}")
                    // Continue with other medications even if one fails
                }
            }

            Timber.d("All medications created, ensuring data is synced")

            Timber.d("Operation completed successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error in AddMedicalVisitWithMedicationsUseCase: ${e.message}")
            throw e
        }
    }
}