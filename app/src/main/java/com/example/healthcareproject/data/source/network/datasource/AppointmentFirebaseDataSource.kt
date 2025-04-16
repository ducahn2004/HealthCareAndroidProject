package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Appointment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AppointmentFirebaseDataSource : AppointmentDataSource {

    override fun observeAll(): Flow<List<Appointment>> = flowOf(emptyList())

    override fun observeById(appointmentId: String): Flow<Appointment?> = flowOf(null)

    override suspend fun getAll(): List<Appointment> = emptyList()

    override suspend fun getById(appointmentId: String): Appointment? = null

    override suspend fun upsert(appointment: Appointment) {}

    override suspend fun upsertAll(appointments: List<Appointment>) {}

    override suspend fun deleteById(appointmentId: String): Int = 0

    override suspend fun deleteAll(): Int = 0
}