package com.example.healthcareproject.data.repository

import com.example.healthcareproject.data.mapper.toExternal
import com.example.healthcareproject.data.mapper.toLocal
import com.example.healthcareproject.data.mapper.toNetwork
import com.example.healthcareproject.data.source.local.dao.MeasurementDao
import com.example.healthcareproject.data.source.network.datasource.MeasurementDataSource
import com.example.healthcareproject.di.ApplicationScope
import com.example.healthcareproject.di.DefaultDispatcher
import com.example.healthcareproject.domain.model.Measurement
import com.example.healthcareproject.domain.model.MeasurementType
import com.example.healthcareproject.domain.repository.MeasurementRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMeasurementRepository @Inject constructor(
    private val networkDataSource: MeasurementDataSource,
    private val localDataSource: MeasurementDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : MeasurementRepository {

    override suspend fun createMeasurement(
        type: MeasurementType,
        value: Float?,
        valueList: List<Float>?,
        measurementTime: LocalDateTime,
        status: Boolean
    ): String {
        val measurementId = withContext(dispatcher) {
            UUID.randomUUID().toString()
        }
        val measurement = Measurement(
            measurementId = measurementId,
            userId = "", // Replace with actual userId logic
            type = type,
            value = value,
            valueList = valueList,
            timestamp = measurementTime
        )
        localDataSource.upsert(measurement.toLocal())
        saveMeasurementsToNetwork()
        return measurementId
    }

    override suspend fun updateMeasurement(
        measurementId: String,
        type: MeasurementType,
        value: Float?,
        valueList: List<Float>?,
        measurementTime: LocalDateTime,
        status: Boolean
    ) {
        val measurement = getMeasurement(measurementId)?.copy(
            type = type,
            value = value,
            valueList = valueList,
            timestamp = measurementTime
        ) ?: throw Exception("Measurement (id $measurementId) not found")

        localDataSource.upsert(measurement.toLocal())
        saveMeasurementsToNetwork()
    }

    override fun getMeasurementsRealtime(userId: String): Flow<List<Measurement>> {
        return networkDataSource.getMeasurementsFirebaseRealtime(userId)
            .map { firebaseMeasurements ->
                val localMeasurements = firebaseMeasurements.toLocal()
                scope.launch {
                    try {
                        localDataSource.upsertAll(localMeasurements)
                    } catch (e: Exception) {
                        // Handle the exception, e.g., log it
                    }
                }
                localMeasurements.toExternal()
            }
    }


    override fun getMeasurementsStream(): Flow<List<Measurement>> {
        return localDataSource.observeAll().map { measurements ->
            withContext(dispatcher) {
                measurements.toExternal()
            }
        }
    }

    override fun getMeasurementStream(measurementId: String): Flow<Measurement?> {
        return localDataSource.observeById(measurementId).map { it.toExternal() }
    }

    override suspend fun getMeasurements(forceUpdate: Boolean): List<Measurement> {
        if (forceUpdate) {
            refresh()
        }
        return withContext(dispatcher) {
            localDataSource.getAll().toExternal()
        }
    }

    override suspend fun refresh() {
        withContext(dispatcher) {
            val remoteMeasurements = networkDataSource.loadMeasurements("") // Pass userId if required
            localDataSource.deleteAll()
            localDataSource.upsertAll(remoteMeasurements.toLocal())
        }
    }

    override suspend fun getMeasurement(measurementId: String, forceUpdate: Boolean): Measurement? {
        if (forceUpdate) {
            refresh()
        }
        return localDataSource.getById(measurementId)?.toExternal()
    }

    override suspend fun refreshMeasurement(measurementId: String) {
        refresh()
    }

    override suspend fun activateMeasurement(measurementId: String) {
        val measurement = getMeasurement(measurementId)?.copy(value = 1f) // Example logic
            ?: throw Exception("Measurement (id $measurementId) not found")
        localDataSource.upsert(measurement.toLocal())
        saveMeasurementsToNetwork()
    }

    override suspend fun deactivateMeasurement(measurementId: String) {
        val measurement = getMeasurement(measurementId)?.copy(value = 0f)
            ?: throw Exception("Measurement (id $measurementId) not found")
        localDataSource.upsert(measurement.toLocal())
        saveMeasurementsToNetwork()
    }

    override suspend fun deleteAllMeasurements() {
        localDataSource.deleteAll()
        saveMeasurementsToNetwork()
    }

    override suspend fun deleteMeasurement(measurementId: String) {
        localDataSource.deleteById(measurementId)
        saveMeasurementsToNetwork()
    }

    private fun saveMeasurementsToNetwork() {
        scope.launch {
            try {
                val localMeasurements = localDataSource.getAll()
                val networkMeasurements = withContext(dispatcher) {
                    localMeasurements.toNetwork()
                }
                networkDataSource.saveMeasurements(networkMeasurements)
            } catch (e: Exception) {
                // Log or handle the exception
            }
        }
    }
}