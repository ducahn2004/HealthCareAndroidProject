package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.source.local.entity.Measurement
import com.example.healthcareproject.data.source.network.datasource.MeasurementDataSource
import kotlinx.coroutines.flow.Flow

class MeasurementRepository(private val measurementDataSource: MeasurementDataSource) {

    fun observeAll(): Flow<List<Measurement>> = measurementDataSource.observeAll()

    fun observeById(measurementId: String): Flow<Measurement?> = measurementDataSource.observeById(measurementId)

    suspend fun getAll(): List<Measurement> = measurementDataSource.getAll()

    suspend fun getById(measurementId: String): Measurement? = measurementDataSource.getById(measurementId)

    suspend fun upsert(measurement: Measurement) = measurementDataSource.upsert(measurement)

    suspend fun upsertAll(measurements: List<Measurement>) = measurementDataSource.upsertAll(measurements)

    suspend fun deleteById(measurementId: String): Int = measurementDataSource.deleteById(measurementId)

    suspend fun deleteAll(): Int = measurementDataSource.deleteAll()
}