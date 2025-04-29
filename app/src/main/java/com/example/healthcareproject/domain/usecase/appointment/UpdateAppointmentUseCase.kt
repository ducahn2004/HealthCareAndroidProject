package com.example.healthcareproject.domain.usecase.appointment

    import com.example.healthcareproject.domain.repository.AppointmentRepository
    import java.time.LocalDateTime
    import javax.inject.Inject

    class UpdateAppointmentUseCase @Inject constructor(
        private val appointmentRepository: AppointmentRepository
    ) {
        suspend operator fun invoke(
            appointmentId: String,
            doctorName: String,
            location: String,
            appointmentTime: LocalDateTime,
            note: String?
        ) {
            appointmentRepository.updateAppointment(
                appointmentId = appointmentId,
                doctorName = doctorName,
                location = location,
                appointmentTime = appointmentTime,
                note = note
            )
        }
    }