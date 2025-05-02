package com.example.healthcareproject.domain.repository

import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.model.MeasurementType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Interface to the data layer for measurements.
 */
interface MeasurementRepository {

    suspend fun createMeasurement(
        type: MeasurementType,
        value: Float?,
        valueList: List<Float>?,
        measurementTime: LocalDateTime,
        status: Boolean
    ): String

    suspend fun updateMeasurement(
        measurementId: String,
        type: MeasurementType,
        value: Float?,
        valueList: List<Float>?,
        measurementTime: LocalDateTime,
        status: Boolean
    )

    fun getMeasurementsRealtime(): Flow<List<Measurement>>

    fun getMeasurementsStream(): Flow<List<Measurement>>

    fun getMeasurementStream(measurementId: String): Flow<Measurement?>

    suspend fun getMeasurements(forceUpdate: Boolean = false): List<Measurement>

    suspend fun refresh()

    suspend fun getMeasurement(measurementId: String, forceUpdate: Boolean = false): Measurement?

    suspend fun refreshMeasurement(measurementId: String)

    suspend fun activateMeasurement(measurementId: String)

    suspend fun deactivateMeasurement(measurementId: String)

    suspend fun deleteAllMeasurements()

    suspend fun deleteMeasurement(measurementId: String)
}