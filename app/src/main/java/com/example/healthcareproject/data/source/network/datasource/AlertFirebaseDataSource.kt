package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseAlert
import kotlinx.coroutines.tasks.await

class AlertFirebaseDataSource : AlertDataSource {

    private val alertsRef = FirebaseService.getReference("alerts")

    override suspend fun writeAlert(alert: FirebaseAlert) {
        alertsRef.child(alert.alertId).setValue(alert).await()
    }

    override suspend fun readAlert(alertId: String): FirebaseAlert? {
        val snapshot = alertsRef.child(alertId).get().await()
        return snapshot.getValue(FirebaseAlert::class.java)
    }

    override suspend fun deleteAlert(alertId: String) {
        alertsRef.child(alertId).removeValue().await()
    }

    override suspend fun updateAlert(alertId: String, alert: FirebaseAlert) {
        alertsRef.child(alertId).setValue(alert).await()
    }

    override suspend fun readAllAlertsByUserId(userId: String): List<FirebaseAlert> {
        val snapshot = alertsRef.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseAlert::class.java) }
    }
}