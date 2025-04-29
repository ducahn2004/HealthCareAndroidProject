package com.example.healthcareproject.domain.usecase.alert

import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class AlertLogicUseCase @Inject constructor(
    private val alertRepository: AlertRepository,
    private val medicationRepository: MedicationRepository
) {
    suspend fun executeLogic(userId: String) {
        val medications = medicationRepository.getMedications()
        val today = LocalDate.now()

        for (medication in medications) {
            if (medication.userId == userId && today in medication.startDate..medication.endDate) {
                medication.timeOfDay.forEach { time ->
                    val alertTime = LocalTime.parse(time)
                    alertRepository.createAlert(
                        title = "Medication Reminder",
                        message = "It's time to take your medication: ${medication.name}",
                        alertTime = alertTime,
                        repeatPattern = RepeatPattern.Daily, // Assuming daily repeat
                        status = true
                    )
                }
            }
        }
    }
}