package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.firebase.FirebaseService
import com.example.healthcareproject.data.source.network.model.FirebaseEmergencyInfo
import kotlinx.coroutines.tasks.await

class EmergencyInfoFirebaseDataSource : EmergencyInfoDataSource {

    private val emergencyInfosRef = FirebaseService.getReference("emergency_infos")

    override suspend fun writeEmergencyInfo(emergencyInfo: FirebaseEmergencyInfo) {
        emergencyInfosRef.child(emergencyInfo.userId).setValue(emergencyInfo).await()
    }

    override suspend fun readEmergencyInfo(userId: String): FirebaseEmergencyInfo? {
        val snapshot = emergencyInfosRef.child(userId).get().await()
        return snapshot.getValue(FirebaseEmergencyInfo::class.java)
    }

    override suspend fun deleteEmergencyInfo(userId: String) {
        emergencyInfosRef.child(userId).removeValue().await()
    }

    override suspend fun updateEmergencyInfo(userId: String, emergencyInfo: FirebaseEmergencyInfo) {
        emergencyInfosRef.child(userId).setValue(emergencyInfo).await()
    }

    override suspend fun readAllEmergencyInfosByUserId(userId: String): List<FirebaseEmergencyInfo> {
        val snapshot = emergencyInfosRef.child(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(FirebaseEmergencyInfo::class.java) }
    }
}