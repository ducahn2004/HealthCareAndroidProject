package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.network.model.FirebaseMeasurement
import kotlinx.coroutines.flow.Flow

interface MeasurementDataSource {

    suspend fun writeMeasurement(measurement: FirebaseMeasurement)

    suspend fun readMeasurements(userId: String): List<FirebaseMeasurement>

    suspend fun deleteMeasurement(userId: String, measurementId: String)

    suspend fun deleteMeasurements(userId: String, measurementIds: List<String>)

    suspend fun readAllMeasurementsByUserId(userId: String): List<FirebaseMeasurement>?

    fun getMeasurementsRealtime(userId: String): Flow<List<FirebaseMeasurement>>

}