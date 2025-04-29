package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseAppointment
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AppointmentFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : AppointmentDataSource {

    private val appointmentsRef = firebaseDatabase.getReference("appointments")

    override suspend fun loadAppointments(userId: String): List<FirebaseAppointment> = try {
        appointmentsRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseAppointment::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading appointments for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveAppointments(appointments: List<FirebaseAppointment>) {
        if (appointments.isEmpty()) return

        try {
            val updates = appointments.associateBy { it.appointmentId }
            appointmentsRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving appointments: ${e.message}", e)
        }
    }
}