package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseEmergencyInfo
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmergencyInfoFirebaseDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) : EmergencyInfoDataSource {

    private val emergencyInfosRef = firebaseDatabase.getReference("emergency_infos")

    override suspend fun loadEmergencyInfos(userId: String): List<FirebaseEmergencyInfo> = try {
        emergencyInfosRef
            .orderByChild("userId")
            .equalTo(userId)
            .get()
            .await()
            .children
            .mapNotNull { it.getValue(FirebaseEmergencyInfo::class.java) }
    } catch (e: Exception) {
        throw Exception("Error loading emergency infos for userId '$userId': ${e.message}", e)
    }

    override suspend fun saveEmergencyInfos(emergencyInfos: List<FirebaseEmergencyInfo>) {
        if (emergencyInfos.isEmpty()) return

        try {
            val updates = emergencyInfos.associateBy { it.emergencyId }
            emergencyInfosRef.updateChildren(updates).await()
        } catch (e: Exception) {
            throw Exception("Error saving emergency infos: ${e.message}", e)
        }
    }
}