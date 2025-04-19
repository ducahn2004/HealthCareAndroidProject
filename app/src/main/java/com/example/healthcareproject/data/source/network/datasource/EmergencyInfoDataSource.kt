package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.EmergencyInfo
import kotlinx.coroutines.flow.Flow

interface EmergencyInfoDataSource {
    fun observeAll(): Flow<List<EmergencyInfo>>

    fun observeById(emergencyInfoId: String): Flow<EmergencyInfo?>

    suspend fun getAll(): List<EmergencyInfo>

    suspend fun getById(emergencyInfoId: String): EmergencyInfo?

    suspend fun upsert(emergencyInfo: EmergencyInfo)

    suspend fun upsertAll(emergencyInfos: List<EmergencyInfo>)

    suspend fun deleteById(emergencyInfoId: String): Int

    suspend fun deleteAll(): Int
}