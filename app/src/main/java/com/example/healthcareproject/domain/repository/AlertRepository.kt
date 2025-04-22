package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Alert
import com.example.healthcareproject.domain.model.RepeatPattern
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

/**
 * Interface to the data layer for alerts.
 */
interface AlertRepository {

    suspend fun createAlert(
        title: String,
        message: String,
        alertTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    ): String

    suspend fun updateAlert(
        alertId: String,
        title: String,
        message: String,
        alertTime: LocalTime,
        repeatPattern: RepeatPattern,
        status: Boolean
    )

    suspend fun deleteAlert(alertId: String)

    suspend fun getAlert(alertId: String, forceUpdate: Boolean = false): Alert?

    suspend fun getAlerts(forceUpdate: Boolean = false): List<Alert>

    fun getAlertsStream(): Flow<List<Alert>>

    fun getAlertsStream(alertId: String): Flow<Alert?>

    suspend fun refresh()

    suspend fun refreshAlert(alertId: String)

    suspend fun activateAlert(alertId: String)

    suspend fun deactivateAlert(alertId: String)

    suspend fun clearInactiveAlerts()

    suspend fun deleteAllAlerts()
}