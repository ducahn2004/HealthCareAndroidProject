package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseAppointment
import kotlinx.coroutines.tasks.await

class AppointmentFirebaseDataSource : AppointmentDataSource {

    private val appointmentsRef = FirebaseService.getReference("appointments")

    override suspend fun writeAppointment(appointment: FirebaseAppointment) {
        appointmentsRef.child(appointment.appointmentId).setValue(appointment).await()
    }

    override suspend fun readAppointment(appointmentId: String): FirebaseAppointment? {
        val snapshot = appointmentsRef.child(appointmentId).get().await()
        return snapshot.getValue(FirebaseAppointment::class.java)
    }

    override suspend fun deleteAppointment(appointmentId: String) {
        appointmentsRef.child(appointmentId).removeValue().await()
    }

    override suspend fun updateAppointment(
        appointmentId: String,
        appointment: FirebaseAppointment
    ) {
        appointmentsRef.child(appointmentId).setValue(appointment).await()
    }

    override suspend fun readAllAppointmentsByUserId(userId: String): List<FirebaseAppointment> {
        val snapshot = appointmentsRef.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseAppointment::class.java) }
    }
}