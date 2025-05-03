package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Measurement
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface to the data layer for measurements.
 */
interface MeasurementRepository {

    suspend fun createMeasurement(
        bpm: Float,
        spO2: Float,
        status: Boolean
    ): String

    suspend fun updateMeasurement(
        measurementId: String,
        bpm: Float,
        spO2: Float,
        status: Boolean
    )

    fun getMeasurementsRealtime(): Flow<List<Measurement>>

    fun getMeasurementsStream(): Flow<List<Measurement>>

    fun getMeasurementStream(measurementId: String): Flow<Measurement?>

    suspend fun getMeasurements(forceUpdate: Boolean = false): List<Measurement>

    suspend fun refresh()

    suspend fun getMeasurement(measurementId: String, forceUpdate: Boolean = false): Measurement?

    suspend fun refreshMeasurement(measurementId: String)

    suspend fun deleteAllMeasurements()

    suspend fun deleteMeasurement(measurementId: String)
}