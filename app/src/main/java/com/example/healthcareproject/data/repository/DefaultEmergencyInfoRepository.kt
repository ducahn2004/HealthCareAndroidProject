package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.EmergencyInfoDao
import com.example.healthcareproject.data.source.network.datasource.EmergencyInfoDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.EmergencyInfo
import com.example.healthcareproject.domain.model.Relationship
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultEmergencyInfoRepository @Inject constructor(
    private val networkDataSource: EmergencyInfoDataSource,
    private val localDataSource: EmergencyInfoDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : EmergencyInfoRepository {

    override suspend fun createEmergencyInfo(
        contactName: String,
        contactNumber: String,
        relationship: Relationship,
        notes: String?,
        status: Boolean
    ): String {
        val emergencyId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val emergencyInfo = EmergencyInfo(
            emergencyId = emergencyId,
            userId = "", // Replace with actual userId logic
            emergencyName = contactName,
            emergencyPhone = contactNumber,
            relationship = relationship,
            priority = if (status) 1 else 0
        )
        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
        return emergencyId
    }

    override suspend fun updateEmergencyInfo(
        emergencyInfoId: String,
        contactName: String,
        contactNumber: String,
        relationship: Relationship,
        notes: String?,
        status: Boolean
    ) {
        val emergencyInfo = getEmergencyInfo(emergencyInfoId)?.copy(
            emergencyName = contactName,
            emergencyPhone = contactNumber,
            relationship = relationship,
            priority = if (status) 1 else 0
        ) ?: throw Exception("EmergencyInfo (id $emergencyInfoId) not found")

        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
    }

    override suspend fun getEmergencyInfos(forceUpdate: Boolean): List<EmergencyInfo> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteEmergencyInfos = networkDataSource.loadEmergencyInfos("") // Pass userId if required
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteEmergencyInfos.toLocal())
        }
    }

    override suspend fun getEmergencyInfo(
        emergencyInfoId: String,
        forceUpdate: Boolean
    ): EmergencyInfo? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(emergencyInfoId)?.toExternal()
    }

    override suspend fun refreshEmergencyInfo(emergencyInfoId: String) {
        refresh()
    }

    override fun getEmergencyInfosStream(): Flow<List<EmergencyInfo>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getEmergencyInfoStream(emergencyInfoId: String): Flow<EmergencyInfo?> {
        return localDataSource.observeById(emergencyInfoId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun activateEmergencyInfo(emergencyInfoId: String) {
        val emergencyInfo = getEmergencyInfo(emergencyInfoId)?.copy(priority = 1)
            ?: throw Exception("EmergencyInfo (id $emergencyInfoId) not found")
        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
    }

    override suspend fun deactivateEmergencyInfo(emergencyInfoId: String) {
        val emergencyInfo = getEmergencyInfo(emergencyInfoId)?.copy(priority = 0)
            ?: throw Exception("EmergencyInfo (id $emergencyInfoId) not found")
        localDataSource.upsert(emergencyInfo.toLocal())
        saveEmergencyInfosToNetwork()
    }

    override suspend fun clearInactiveEmergencyInfos() {
        localDataSource.deleteByUserId("") // Adjust logic to delete inactive emergency infos
        saveEmergencyInfosToNetwork()
    }

    override suspend fun deleteAllEmergencyInfos() {
        localDataSource.deleteAll()
        saveEmergencyInfosToNetwork()
    }

    override suspend fun deleteEmergencyInfo(emergencyInfoId: String) {
        localDataSource.deleteById(emergencyInfoId)
        saveEmergencyInfosToNetwork()
    }

    private fun saveEmergencyInfosToNetwork() {
        scope.launch {
            try {
                val localEmergencyInfos = localDataSource.getAll()
                val networkEmergencyInfos = withContext(dispatcher) {
                    localEmergencyInfos.toNetwork()
                }
                networkDataSource.saveEmergencyInfos(networkEmergencyInfos)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}