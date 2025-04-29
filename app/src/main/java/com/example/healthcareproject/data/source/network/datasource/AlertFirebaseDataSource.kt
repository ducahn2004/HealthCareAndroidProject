package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseAlert
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlertFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : AlertDataSource {

    private val alertsRef = firebaseDatabase.getReference("alerts")

    override suspend fun loadAlerts(userId: String): List<FirebaseAlert> = try {
        alertsRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseAlert::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading alerts for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveAlerts(alerts: List<FirebaseAlert>) {
        if (alerts.isEmpty()) return

        try {
            val updates = alerts.associateBy { it.alertId }
            alertsRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving alerts: ${e.message}", e)
        }
    }
}