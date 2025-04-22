package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.AppointmentDao
import com.example.healthcareproject.data.source.network.datasource.AppointmentDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.Appointment
import com.example.healthcareproject.domain.repository.AppointmentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAppointmentRepository @Inject constructor(
    private val networkDataSource: AppointmentDataSource,
    private val localDataSource: AppointmentDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : AppointmentRepository {

    override suspend fun createAppointment(
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    ): String {
        val appointmentId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val appointment = Appointment(
            appointmentId = appointmentId,
            userId = "", // Replace with actual userId logic
            visitId = null, // Set visitId if applicable
            doctorName = doctorName,
            location = location,
            appointmentTime = appointmentTime,
            note = note
        )
        localDataSource.upsert(appointment.toLocal())
        saveAppointmentsToNetwork()
        return appointmentId
    }

    override suspend fun updateAppointment(
        appointmentId: String,
        doctorName: String,
        location: String,
        appointmentTime: LocalDateTime,
        note: String?
    ) {
        val appointment = getAppointment(appointmentId)?.copy(
            doctorName = doctorName,
            location = location,
            appointmentTime = appointmentTime,
            note = note
        ) ?: throw Exception("Appointment (id $appointmentId) not found")

        localDataSource.upsert(appointment.toLocal())
        saveAppointmentsToNetwork()
    }

    override suspend fun deleteAppointment(appointmentId: String) {
        localDataSource.deleteById(appointmentId)
        saveAppointmentsToNetwork()
    }

    override suspend fun getAppointment(appointmentId: String, forceUpdate: Boolean): Appointment? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(appointmentId)?.toExternal()
    }

    override suspend fun getAppointments(forceUpdate: Boolean): List<Appointment> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override fun getAppointmentsStream(): Flow<List<Appointment>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getAppointmentStream(appointmentId: String): Flow<Appointment?> {
        return localDataSource.observeById(appointmentId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun deleteAllAppointments() {
        localDataSource.deleteAll()
        saveAppointmentsToNetwork()
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteAppointments = networkDataSource.loadAppointments("") // Pass userId if required
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteAppointments.toLocal())
        }
    }

    override suspend fun refreshAppointment(appointmentId: String) {
        refresh()
    }

    private fun saveAppointmentsToNetwork() {
        scope.launch {
            try {
                val localAppointments = localDataSource.getAll()
                val networkAppointments = withContext(dispatcher) {
                    localAppointments.toNetwork()
                }
                networkDataSource.saveAppointments(networkAppointments)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}