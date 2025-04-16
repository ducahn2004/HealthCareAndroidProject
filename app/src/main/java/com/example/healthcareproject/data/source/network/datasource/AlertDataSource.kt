package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Alert
import kotlinx.coroutines.flow.Flow

interface AlertDataSource {
    fun observeAll(): Flow<List<Alert>>

    fun observeById(alertId: String): Flow<Alert?>

    suspend fun getAll(): List<Alert>

    suspend fun getById(alertId: String): Alert?

    suspend fun upsert(alert: Alert)

    suspend fun upsertAll(alerts: List<Alert>)

    suspend fun deleteById(alertId: String): Int

    suspend fun deleteAll(): Int
}