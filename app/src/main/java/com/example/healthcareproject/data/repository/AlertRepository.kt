package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.Alert
import com.example.healthcareproject.data.source.network.datasource.AlertDataSource
import kotlinx.coroutines.flow.Flow

class AlertRepository(private val alertDataSource: AlertDataSource) {

    fun observeAll(): Flow<List<Alert>> = alertDataSource.observeAll()

    fun observeById(alertId: String): Flow<Alert?> = alertDataSource.observeById(alertId)

    suspend fun getAll(): List<Alert> = alertDataSource.getAll()

    suspend fun getById(alertId: String): Alert? = alertDataSource.getById(alertId)

    suspend fun upsert(alert: Alert) = alertDataSource.upsert(alert)

    suspend fun upsertAll(alerts: List<Alert>) = alertDataSource.upsertAll(alerts)

    suspend fun deleteById(alertId: String): Int = alertDataSource.deleteById(alertId)

    suspend fun deleteAll(): Int = alertDataSource.deleteAll()
}