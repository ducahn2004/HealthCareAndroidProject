package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseEmergencyInfo

interface EmergencyInfoDataSource {

    suspend fun writeEmergencyInfo(emergencyInfo: FirebaseEmergencyInfo)

    suspend fun readEmergencyInfo(userId: String): FirebaseEmergencyInfo?

    suspend fun deleteEmergencyInfo(userId: String)

    suspend fun updateEmergencyInfo(userId: String, emergencyInfo: FirebaseEmergencyInfo)

    suspend fun readAllEmergencyInfosByUserId(userId: String): List<FirebaseEmergencyInfo>?
}