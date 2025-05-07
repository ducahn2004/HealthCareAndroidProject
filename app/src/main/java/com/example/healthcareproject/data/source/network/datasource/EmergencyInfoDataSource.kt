package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseEmergencyInfo

interface EmergencyInfoDataSource {

    suspend fun loadEmergencyInfos(userId: String): List<FirebaseEmergencyInfo>

    suspend fun saveEmergencyInfos(emergencyInfos: List<FirebaseEmergencyInfo>)

    suspend fun deleteEmergencyInfo(emergencyInfoId: String)
}