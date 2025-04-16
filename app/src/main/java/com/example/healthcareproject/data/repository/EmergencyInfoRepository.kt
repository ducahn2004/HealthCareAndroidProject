package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.EmergencyInfo
import com.example.healthcareproject.data.source.network.datasource.EmergencyInfoDataSource
import kotlinx.coroutines.flow.Flow

class EmergencyInfoRepository(private val emergencyInfoDataSource: EmergencyInfoDataSource) {

    fun observeAll(): Flow<List<EmergencyInfo>> = emergencyInfoDataSource.observeAll()

    fun observeById(emergencyInfoId: String): Flow<EmergencyInfo?> = emergencyInfoDataSource.observeById(emergencyInfoId)

    suspend fun getAll(): List<EmergencyInfo> = emergencyInfoDataSource.getAll()

    suspend fun getById(emergencyInfoId: String): EmergencyInfo? = emergencyInfoDataSource.getById(emergencyInfoId)

    suspend fun upsert(emergencyInfo: EmergencyInfo) = emergencyInfoDataSource.upsert(emergencyInfo)

    suspend fun upsertAll(emergencyInfos: List<EmergencyInfo>) = emergencyInfoDataSource.upsertAll(emergencyInfos)

    suspend fun deleteById(emergencyInfoId: String): Int = emergencyInfoDataSource.deleteById(emergencyInfoId)

    suspend fun deleteAll(): Int = emergencyInfoDataSource.deleteAll()
}