package com.example.healthcareproject.data.repository

import com.example.healthcareproject.domain.model.Appointment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface to the data layer for appointments.
 */
interface AppointmentRepository {

    suspend fun createAppointment(
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    ): String

    suspend fun updateAppointment(
        appointmentId: String,
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    )

    suspend fun deleteAppointment(appointmentId: String)

    suspend fun getAppointment(appointmentId: String, forceUpdate: Boolean = false): Appointment?

    suspend fun getAppointments(forceUpdate: Boolean = false): List<Appointment>

    fun getAppointmentsStream(): Flow<List<Appointment>>

    fun getAppointmentStream(appointmentId: String): Flow<Appointment?>

    suspend fun deleteAllAppointments()

    suspend fun refresh()

    suspend fun refreshAppointment(appointmentId: String)
}