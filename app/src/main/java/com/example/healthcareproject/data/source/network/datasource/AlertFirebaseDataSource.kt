package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Alert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AlertFirebaseDataSource : AlertDataSource {

    override fun observeAll(): Flow<List<Alert>> = flowOf(emptyList())

    override fun observeById(alertId: String): Flow<Alert?> = flowOf(null)

    override suspend fun getAll(): List<Alert> = emptyList()

    override suspend fun getById(alertId: String): Alert? = null

    override suspend fun upsert(alert: Alert) {}

    override suspend fun upsertAll(alerts: List<Alert>) {}

    override suspend fun deleteById(alertId: String): Int = 0

    override suspend fun deleteAll(): Int = 0
}