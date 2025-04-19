package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseAppointment

interface AppointmentDataSource {

    suspend fun writeAppointment(appointment: FirebaseAppointment)

    suspend fun readAppointment(appointmentId: String): FirebaseAppointment?

    suspend fun deleteAppointment(appointmentId: String)

    suspend fun updateAppointment(appointmentId: String, appointment: FirebaseAppointment)

    suspend fun readAllAppointmentsByUserId(userId: String): List<FirebaseAppointment>?
}