package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Measurement
import kotlinx.coroutines.flow.Flow

interface MeasurementDataSource {
    fun observeAll(): Flow<List<Measurement>>

    fun observeById(measurementId: String): Flow<Measurement?>

    suspend fun getAll(): List<Measurement>

    suspend fun getById(measurementId: String): Measurement?

    suspend fun upsert(measurement: Measurement)

    suspend fun upsertAll(measurements: List<Measurement>)

    suspend fun deleteById(measurementId: String): Int

    suspend fun deleteAll(): Int
}