package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.AlertDao
import com.example.healthcareproject.data.source.network.datasource.AuthDataSource
import com.example.healthcareproject.data.source.network.datasource.AlertDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.repository.AlertRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAlertRepository @Inject constructor(
    private val networkDataSource: AlertDataSource,
    private val localDataSource: AlertDao,
    private val authDataSource: AuthDataSource,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : AlertRepository {

    private val userId: String
        get() = authDataSource.getCurrentUserId() ?: throw Exception("User not logged in")

    override suspend fun createAlert(
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean
    ): String {
        val alertId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val alert = Alert(
            alertId = alertId,
            userId = userId,
            measurementId = measurementId,
            emergencyId = emergencyId,
            triggerReason = triggerReason,
            contacted = contacted,
            timestamp = LocalDateTime.now()
        )
        localDataSource.upsert(alert.toLocal())
        saveAlertToNetwork()

        Timber.tag("DefaultAlertRepository").d("Alert created with ID: $alertId")

        return alertId
    }

    override suspend fun updateAlert(
        alertId: String,
        contacted: Boolean,
    ) {
        val alert = getAlert(alertId)?.copy(
            contacted = contacted,
        ) ?: throw Exception("Alert (id $alertId) not found")

        localDataSource.upsert(alert.toLocal())
        saveAlertToNetwork()
    }

    override fun getAlertListStream(forceUpdate: Boolean): Flow<List<Alert>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getAlertStream(alertId: String, forceUpdate: Boolean): Flow<Alert?> {
        return localDataSource.observeById(alertId)
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override suspend fun getAlertList(forceUpdate: Boolean): List<Alert> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteAlerts = networkDataSource.loadAlerts(userId)
            val localAlerts = localDataSource.upsertAll(remoteAlerts.toLocal())
            saveAlertToNetwork()
        }
    }

    override suspend fun getAlert(alertId: String, forceUpdate: Boolean): Alert? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(alertId)?.toExternal()
    }

    override suspend fun refreshAlert(alertId: String) {
        refresh()
    }

    override suspend fun activateAlert(alertId: String) {
        val alert = getAlert(alertId)?.copy(contacted = true)
            ?: throw Exception("Alert (id $alertId) not found")
        localDataSource.upsert(alert.toLocal())
        saveAlertToNetwork()
    }

    override suspend fun deactivateAlert(alertId: String) {
        val alert = getAlert(alertId)?.copy(contacted = false)
            ?: throw Exception("Alert (id $alertId) not found")
        localDataSource.upsert(alert.toLocal())
        saveAlertToNetwork()
    }

    override suspend fun clearInactiveAlerts() {
        localDataSource.deleteByUserId(userId)
        saveAlertToNetwork()
    }

    override suspend fun deleteAllAlerts() {
        localDataSource.deleteAll()
        saveAlertToNetwork()
    }

    override suspend fun deleteAlert(alertId: String) {
        localDataSource.deleteById(alertId)
        saveAlertToNetwork()
    }

    private fun saveAlertToNetwork() {
        scope.launch {
            try {
                val localAlerts = localDataSource.getAll()
                val networkAlerts = withContext(dispatcher) {
                    localAlerts.toNetwork()
                }
                networkDataSource.saveAlerts(networkAlerts)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}