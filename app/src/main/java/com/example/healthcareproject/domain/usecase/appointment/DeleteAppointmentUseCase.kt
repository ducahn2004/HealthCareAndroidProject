package com.example.healthcareproject.domain.usecase.appointment

import com.example.healthcareproject.domain.repository.AppointmentRepository
import javax.inject.Inject

class DeleteAppointmentUseCase @Inject constructor(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(appointmentId: String) {
        appointmentRepository.deleteAppointment(appointmentId)
    }
}