package com.example.healthcareproject.domain.usecase.medication

import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.AlertRepository
import com.example.healthcareproject.domain.repository.MedicationRepository
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class MedicationReminderLogicUseCase @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val alertRepository: AlertRepository
) {
    suspend fun execute() {
        val medications = medicationRepository.getMedications()
        val today = LocalDate.now()

        medications.filter {today in it.startDate..it.endDate }
            .forEach { medication ->
                medication.timeOfDay.forEach { time ->
                    val alertTime = LocalTime.parse(time)
                    alertRepository.createAlert(
                        title = "Medication Reminder",
                        message = "It's time to take your medication: ${medication.name}",
                        alertTime = alertTime,
                        repeatPattern = RepeatPattern.Daily,
                        status = true
                    )
                }
            }
    }
}