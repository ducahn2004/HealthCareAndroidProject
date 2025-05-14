package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Alert
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface to the data layer for Alerts.
 */
interface AlertRepository {

    suspend fun createAlert(
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    ): String

    suspend fun updateAlert(
        alertId: String,
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    )

    fun getAlertListStream(forceUpdate: Boolean = false): Flow<List<Alert>>

    fun getAlertStream(alertId: String, forceUpdate: Boolean = false): Flow<Alert?>

    suspend fun getAlertList(forceUpdate: Boolean = false): List<Alert>

    suspend fun refresh()

    suspend fun getAlert(alertId: String, forceUpdate: Boolean = false): Alert?

    suspend fun refreshAlert(alertId: String)

    suspend fun activateAlert(alertId: String)

    suspend fun deactivateAlert(alertId: String)

    suspend fun clearInactiveAlerts()

    suspend fun deleteAllAlerts()

    suspend fun deleteAlert(alertId: String)
}