package com.example.healthcareproject.data.source.network.datasource

import com.example.healthcareproject.data.source.local.entity.Measurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MeasurementFirebaseDataSource : MeasurementDataSource {

    override fun observeAll(): Flow<List<Measurement>> = flowOf(emptyList())

    override fun observeById(measurementId: String): Flow<Measurement?> = flowOf(null)

    override suspend fun getAll(): List<Measurement> = emptyList()

    override suspend fun getById(measurementId: String): Measurement? = null

    override suspend fun upsert(measurement: Measurement) {}

    override suspend fun upsertAll(measurements: List<Measurement>) {}

    override suspend fun deleteById(measurementId: String): Int = 0

    override suspend fun deleteAll(): Int = 0
}