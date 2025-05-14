package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseAlert
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlertFirebaseDataSource @Inject constructor(
    firebaseDatabase: FirebaseDatabase
) : AlertDataSource {

    private val alertRef = firebaseDatabase.getReference("alert")

    override suspend fun loadAlerts(userId: String): List<FirebaseAlert> = try {
        alertRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseAlert::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading alert data for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveAlerts(alertList: List<FirebaseAlert>) {
        if (alertList.isEmpty()) return

        try {
            val updates = alertList.associateBy { it.alertId }
            alertRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving alert data: ${e.message}", e)
        }
    }
}