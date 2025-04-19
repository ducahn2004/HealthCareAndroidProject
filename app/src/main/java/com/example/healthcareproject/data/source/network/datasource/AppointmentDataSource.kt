package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Appointment
import kotlinx.coroutines.flow.Flow

interface AppointmentDataSource {
    fun observeAll(): Flow<List<Appointment>>

    fun observeById(appointmentId: String): Flow<Appointment?>

    suspend fun getAll(): List<Appointment>

    suspend fun getById(appointmentId: String): Appointment?

    suspend fun upsert(appointment: Appointment)

    suspend fun upsertAll(appointments: List<Appointment>)

    suspend fun deleteById(appointmentId: String): Int

    suspend fun deleteAll(): Int
}