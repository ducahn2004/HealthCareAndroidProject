package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.SosDao
import com.example.healthcareproject.data.source.network.datasource.SosDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.Sos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSosRepository @Inject constructor(
    private val networkDataSource: SosDataSource,
    private val localDataSource: SosDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : SosRepository {

    override suspend fun createSos(
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    ): String {
        val sosId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val sos = Sos(
            sosId = sosId,
            userId = "", // Replace with actual userId logic
            measurementId = measurementId,
            emergencyId = emergencyId,
            triggerReason = triggerReason,
            contacted = contacted,
            timestamp = timestamp
        )
        localDataSource.upsert(sos.toLocal())
        saveSosToNetwork()
        return sosId
    }

    override suspend fun updateSos(
        sosId: String,
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    ) {
        val sos = getSos(sosId)?.copy(
            measurementId = measurementId,
            emergencyId = emergencyId,
            triggerReason = triggerReason,
            contacted = contacted,
            timestamp = timestamp
        ) ?: throw Exception("Sos (id $sosId) not found")

        localDataSource.upsert(sos.toLocal())
        saveSosToNetwork()
    }

    override fun getSosListStream(forceUpdate: Boolean): Flow<List<Sos>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getSosStream(sosId: String, forceUpdate: Boolean): Flow<Sos?> {
        return localDataSource.observeById(sosId)
            .map { it?.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getSosList(forceUpdate: Boolean): List<Sos> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteSos = networkDataSource.loadSos("") // Pass userId if required
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteSos.toLocal())
        }
    }

    override suspend fun getSos(sosId: String, forceUpdate: Boolean): Sos? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(sosId)?.toExternal()
    }

    override suspend fun refreshSos(sosId: String) {
        refresh()
    }

    override suspend fun activateSos(sosId: String) {
        val sos = getSos(sosId)?.copy(contacted = true)
            ?: throw Exception("Sos (id $sosId) not found")
        localDataSource.upsert(sos.toLocal())
        saveSosToNetwork()
    }

    override suspend fun deactivateSos(sosId: String) {
        val sos = getSos(sosId)?.copy(contacted = false)
            ?: throw Exception("Sos (id $sosId) not found")
        localDataSource.upsert(sos.toLocal())
        saveSosToNetwork()
    }

    override suspend fun clearInactiveSos() {
        localDataSource.deleteByUserId("") // Adjust logic to delete inactive SOS
        saveSosToNetwork()
    }

    override suspend fun deleteAllSos() {
        localDataSource.deleteAll()
        saveSosToNetwork()
    }

    override suspend fun deleteSos(sosId: String) {
        localDataSource.deleteById(sosId)
        saveSosToNetwork()
    }

    private fun saveSosToNetwork() {
        scope.launch {
            try {
                val localSos = localDataSource.getAll()
                val networkSos = withContext(dispatcher) {
                    localSos.toNetwork()
                }
                networkDataSource.saveSos(networkSos)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}