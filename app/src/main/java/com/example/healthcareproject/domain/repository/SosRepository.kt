package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Sos
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface to the data layer for SOS.
 */
interface SosRepository {

    suspend fun createSos(
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    ): String

    suspend fun updateSos(
        sosId: String,
        measurementId: String?,
        emergencyId: String?,
        triggerReason: String,
        contacted: Boolean,
        timestamp: LocalDateTime
    )

    fun getSosListStream(forceUpdate: Boolean = false): Flow<List<Sos>>

    fun getSosStream(sosId: String, forceUpdate: Boolean = false): Flow<Sos?>

    suspend fun getSosList(forceUpdate: Boolean = false): List<Sos>

    suspend fun refresh()

    suspend fun getSos(sosId: String, forceUpdate: Boolean = false): Sos?

    suspend fun refreshSos(sosId: String)

    suspend fun activateSos(sosId: String)

    suspend fun deactivateSos(sosId: String)

    suspend fun clearInactiveSos()

    suspend fun deleteAllSos()

    suspend fun deleteSos(sosId: String)
}