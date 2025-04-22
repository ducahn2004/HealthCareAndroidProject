package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.AlertDao
import com.example.healthcareproject.data.source.network.datasource.AlertDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.model.RepeatPattern
import com.example.healthcareproject.domain.repository.AlertRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAlertRepository @Inject constructor(
    private val networkDataSource: AlertDataSource,
    private val localDataSource: AlertDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : AlertRepository {

    override suspend fun createAlert(
        title: String,
        message: String,
        alertTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ): String {
        val alertId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val alert = Alert(
            alertId = alertId,
            userId = "", // Replace with actual userId logic
            title = title,
            message = message,
            alertTime = alertTime,
            repeatPattern = repeatPattern,
            status = status,
            createdAt = LocalDateTime.now()
        )
        localDataSource.upsert(alert.toLocal())
        saveAlertsToNetwork()
        return alertId
    }

    override suspend fun updateAlert(
        alertId: String,
        title: String,
        message: String,
        alertTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ) {
        val alert = getAlert(alertId)?.copy(
            title = title,
            message = message,
            alertTime = alertTime,
            repeatPattern = repeatPattern,
            status = status
        ) ?: throw Exception("Alert (id $alertId) not found")

        localDataSource.upsert(alert.toLocal())
        saveAlertsToNetwork()
    }

    override suspend fun deleteAlert(alertId: String) {
        localDataSource.deleteById(alertId)
        saveAlertsToNetwork()
    }

    override suspend fun getAlert(alertId: String, forceUpdate: Boolean): Alert? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(alertId)?.toExternal()
    }

    override suspend fun getAlerts(forceUpdate: Boolean): List<Alert> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override fun getAlertsStream(): Flow<List<Alert>> {
        return localDataSource.observeAll()
            .map { it.toExternal() }
            .flowOn(dispatcher)
    }

    override fun getAlertsStream(alertId: String): Flow<Alert?> {
        return localDataSource.observeById(alertId)
            .map { it?.toExternal() }
            .flowOn(dispatcher)
    }


    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteAlerts = networkDataSource.loadAlerts("") // Pass userId if required
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteAlerts.toLocal())
        }
    }

    override suspend fun refreshAlert(alertId: String) {
        refresh()
    }

    override suspend fun activateAlert(alertId: String) {
        val alert = getAlert(alertId)?.copy(status = true)
            ?: throw Exception("Alert (id $alertId) not found")
        localDataSource.upsert(alert.toLocal())
        saveAlertsToNetwork()
    }

    override suspend fun deactivateAlert(alertId: String) {
        val alert = getAlert(alertId)?.copy(status = false)
            ?: throw Exception("Alert (id $alertId) not found")
        localDataSource.upsert(alert.toLocal())
        saveAlertsToNetwork()
    }

    override suspend fun clearInactiveAlerts() {
        localDataSource.deleteInactive()
        saveAlertsToNetwork()
    }

    override suspend fun deleteAllAlerts() {
        localDataSource.deleteAll()
        saveAlertsToNetwork()
    }

    private fun saveAlertsToNetwork() {
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