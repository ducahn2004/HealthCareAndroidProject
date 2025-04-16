package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.Appointment
import com.example.healthcareproject.data.source.network.datasource.AppointmentDataSource
import kotlinx.coroutines.flow.Flow

class AppointmentRepository(private val appointmentDataSource: AppointmentDataSource) {

    fun observeAll(): Flow<List<Appointment>> = appointmentDataSource.observeAll()

    fun observeById(appointmentId: String): Flow<Appointment?> = appointmentDataSource.observeById(appointmentId)

    suspend fun getAll(): List<Appointment> = appointmentDataSource.getAll()

    suspend fun getById(appointmentId: String): Appointment? = appointmentDataSource.getById(appointmentId)

    suspend fun upsert(appointment: Appointment) = appointmentDataSource.upsert(appointment)

    suspend fun upsertAll(appointments: List<Appointment>) = appointmentDataSource.upsertAll(appointments)

    suspend fun deleteById(appointmentId: String): Int = appointmentDataSource.deleteById(appointmentId)

    suspend fun deleteAll(): Int = appointmentDataSource.deleteAll()
}