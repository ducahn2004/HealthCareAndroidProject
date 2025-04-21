package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseAppointment

interface AppointmentDataSource {

    suspend fun loadAppointments(userId: String): List<FirebaseAppointment>

    suspend fun saveAppointments(appointments: List<FirebaseAppointment>)
}