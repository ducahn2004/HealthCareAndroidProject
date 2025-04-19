package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.EmergencyInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class EmergencyInfoFirebaseDataSource : EmergencyInfoDataSource {

    override fun observeAll(): Flow<List<EmergencyInfo>> = flowOf(emptyList())

    override fun observeById(emergencyInfoId: String): Flow<EmergencyInfo?> = flowOf(null)

    override suspend fun getAll(): List<EmergencyInfo> = emptyList()

    override suspend fun getById(emergencyInfoId: String): EmergencyInfo? = null

    override suspend fun upsert(emergencyInfo: EmergencyInfo) {}

    override suspend fun upsertAll(emergencyInfos: List<EmergencyInfo>) {}

    override suspend fun deleteById(emergencyInfoId: String): Int = 0

    override suspend fun deleteAll(): Int = 0
}